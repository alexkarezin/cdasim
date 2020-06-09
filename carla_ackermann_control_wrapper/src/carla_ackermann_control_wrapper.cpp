/*
 * Copyright (C) 2019-2020 LEIDOS.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

#include "carla_ackermann_control_wrapper.h"

namespace carla_ackermann_control_wrapper
{
CarlaAckermannControlWrapper::CarlaAckermannControlWrapper(){}

void CarlaAckermannControlWrapper::init()
{
    ROS_INFO("Initalizing carla ackermann control wrapper node...");

    // Load parameters
    wheel_base_ = pnh_.param<double>("vehicle_wheel_base", 2.79);
    max_steer_angle_ = pnh_.param<double>("max_steer_angle", 29.2);
    max_speed_ = pnh_.param<double>("max_speed", 50);
    vehicle_mass_ = pnh_.param<double>("vehicle_weight", 1500);
    max_accel_ = pnh_.param<double>("acceleration_limit", 3.0);
    max_decel_ = pnh_.param<double>("deceleration_limit", 6.0);

    // Set driver type
    driver_status_.controller = true;

    // Initialize all subscribers
    vehicle_cmd_sub_ = nh_.subscribe("vehicle_cmd", 1, &CarlaAckermannControlWrapper::vehicle_cmd_cb, this);
    carla_enabled_sub_ = nh_.subscribe("carla_enabled", 1, &CarlaAckermannControlWrapper::publish_robot_status, this);
    pose_sub_ = nh_.subscribe("current_pose", 1, &CarlaAckermannControlWrapper::pose_cb, this);
    twist_sub_ = nh_.subscribe("current_velocity", 1, &CarlaAckermannControlWrapper::twist_cd, this);

    // Initialize all publishers
    ackermanndrive_pub_ = nh_.advertise<cav_msgs::CarlaEgoVehicleControl>("ackermann_cmd", 1);
    robot_status_pub_ = nh_.advertise<cav_msgs::RobotEnabled>("controller/robot_status", 1);
    vehicle_info_pub_ = nh_.advertise<cav_msgs::CarlaEgoVehicleInfo>("carla_ego_info", 1);
    vehicle_status_pub_ = nh_.advertise<cav_msgs::CarlaEgoVehicleStatus>("carla_ego_status", 1);

    // Publish ego vehicle info
    ego_info_.rolename = "ego_vehicle";
    for (auto wheel : ego_info_.wheels){
        wheel.max_steer_angle = max_steer_angle_;
        ego_info_.wheels.push_back(wheel);
    }
    ego_info_.max_speed = max_speed_;
    ego_info_.max_acceleration = max_accel_;
    ego_info_.max_deceleration = max_decel_;
    ego_info_.mass = vehicle_mass_;
    vehicle_info_pub_.publish(ego_info_);

    // Publish ego vehicle status
    ego_status_.velocity = current_speed_;
    ego_status_.orientation = pose_msg_->pose.orientation;
    vehicle_status_pub_.publish(ego_status_);
}

// Publish robotic status
void CarlaAckermannControlWrapper::publish_robot_status(const cav_msgs::CarlaEnabledConstPtr& msg)
{
    robotic_status_.robot_active = msg->carla_enabled;
    robot_status_pub_.publish(robotic_status_);
    update_controller_health_status();
}

void CarlaAckermannControlWrapper::update_controller_health_status()
{
    if (robotic_status_.robot_active)
    {
        driver_status_.status = cav_msgs::DriverStatus::OPERATIONAL;
    } else {
        driver_status_.status = cav_msgs::DriverStatus::OFF;
    }
}

void CarlaAckermannControlWrapper::pose_cb(const geometry_msgs::PoseStampedConstPtr& msg)
{
    pose_msg_ = msg;
}

void CarlaAckermannControlWrapper::twist_cd(const geometry_msgs::TwistStampedConstPtr& msg)
{
    current_speed_ = msg->twist.linear.x;
}

void CarlaAckermannControlWrapper::vehicle_cmd_cb(const autoware_msgs::VehicleCmd::ConstPtr& vehicle_cmd)
{
    vehicle_cmd_ = *vehicle_cmd;
    ackermann_drive_ = worker_.update_ackermanndrive_cmd(vehicle_cmd_, wheel_base_);
    ackermanndrive_pub_.publish(ackermann_drive_);
}

int CarlaAckermannControlWrapper::run()
{
    init();
    ROS_INFO("Successfully launched node.");

    return 0;
}
}