Logging into Docker:
docker login

Running RabbitMQ in Docker:

docker pull rabbitmq:3-management
docker run --rm -it --hostname my-rabbit -p 15672:15672 -p 5672:5672 rabbitmq:3-management
