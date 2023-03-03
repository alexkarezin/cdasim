 #   Copyright (C) 2022 LEIDOS.
 #
 #   Licensed under the Apache License, Version 2.0 (the "License"); you may not
 #   use this file except in compliance with the License. You may obtain a copy of
 #   the License at
 #
 #   http://www.apache.org/licenses/LICENSE-2.0
 #
 #   Unless required by applicable law or agreed to in writing, software
 #   distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 #   WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 #   License for the specific language governing permissions and limitations under
 #   the License.
 #

import sys
import os

if 'SUMO_HOME' in os.environ:
    sys.path.append(os.path.join(os.environ['SUMO_HOME'], 'tools'))
else:
    sys.exit("please declare environment variable 'SUMO_HOME'")

# must be declare after getting SUMO_HOME env var
import traci
from contextlib import contextmanager

class SumoConnector:
    def __init__(self, port, order_num):
        """
        Initialize SumoConnector object

        Parameters:
        - port: The port number to connect to SUMO, which should be consistent with SUMO simulator setting
        type: string

        - order_num: The priority order number of the connection to SUMO, which should be consistent with other SUMO connections if any
        type: string
        """
        self.port = port
        self.order_num = order_num
        self.traci = traci

    def traci_get_step_length(self):
        return traci.simulation.getDeltaT()

    @contextmanager
    def traci_handler(self):
        """
        Initialize traci
        """
        self.traci.init(int(self.port))
        self.traci.setOrder(self.order_num)
        yield
        self.traci.close()

    def tick(self):
        """
        Advance SUMO time via traci
        """
        self.traci.simulationStep()

    def set_traffic_light_status_to_SUMO(self, sumo_tl_id, tl_state_string):
        """
        Set traffic light status to SUMO

        Parameters:
        - sumo_tl_id: SUMO traffic light ID (example: 1008)
        type: int

        - tl_state_string: traffic light state string for SUMO (example: "rrrrrr")
        type: string
        """
        traci.trafficlight.setRedYellowGreenState(str(sumo_tl_id), tl_state_string)

    def get_detector_status_from_SUMO(self):
        """
        Get detector status from SUMO
        """
        ## TBD
        pass