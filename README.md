# Welcome to Sleepy.io!
This repository is meant as both a playground for the Sleepr API, as well as some dynamic analysis for past Sleeper leagues and teams.

## Local Development
Sleepy relies on a Redis cache to hold objects from the Sleeper API to reduce latency. To run a local Redis instance, 
please ensure that Docker is running on your machine and run the following command: 

```docker run -p 6379:6379 --name sleepy-redis-server -d redis --requirepass password```

This should start a local docker container that the application can connect to. For more information, check out this
[blog post](https://www.glennprince.com/blog/setting-up-a-redis-server/).
