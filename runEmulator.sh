#
# Copyright 2022 Tolam Earth
#
# Licensed under the Apache License, Version 2.0 (the "License"); you may not
# use this file except in compliance with the License. You may obtain a copy of
# the License at
#
# http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
# WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
# License for the specific language governing permissions and limitations under
# the License.
#

gcloud beta emulators pubsub start --project=hem-integration-services

# init env
$(gcloud beta emulators pubsub env-init)

# set python env.
# clone sample code from https://github.com/googleapis/python-pubsub project
cd /home/roman/bin/googleapis/python-pubsub/samples/snippets
pip install -r requirements.txt

# create publisher
python3 publisher.py hem-integration-services create nft_details
python3 publisher.py hem-integration-services create classifier

# create listener
python3 subscriber.py hem-integration-services create nft_details nft_details_listener
python3 subscriber.py hem-integration-services create classifier class_listener


# IntelliJ run env
GOOGLE_CLOUD_PROJECT=hem-integration-services;PUBSUB_EMULATOR_HOST=localhost:8085

# test IntelliJ env
GOOGLE_CLOUD_PROJECT=hem-integration-services