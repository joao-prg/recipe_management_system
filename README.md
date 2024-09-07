# Recipe Management System

This project is a Recipe Management System built with Spring Boot. The application allows users to create, read, update, and delete recipes. It also supports user authentication and recipe categorization. The project includes a Docker setup for database management and integration testing.

## Table of Contents

- [Prerequisites](#prerequisites)
- [Environment Setup](#environment-setup)
- [Building the Project](#building-the-project)
- [Running the Application](#running-the-application)
- [Running Tests](#running-tests)
- [Makefile Targets](#makefile-targets)
- [Deploy Jenkins on AWS](#deploy-jenkins-on-aws)
- [License](#license)

## Prerequisites

Ensure you have the following installed:

- Java 11 or higher
- Gradle
- Docker
- Docker Compose

## Environment Setup

The project uses environment variables for configuration. You can define these in a `.env` file at the root of the project.

Example `.env` file:

```bash
ADMIN_EMAIL=admin@example.com
ADMIN_PASSWORD=securepassword
POSTGRES_USER=postgres
POSTGRES_PASSWORD=postgres
```

To check the current environment variables, you can run the following command:

```sh
make env
```

## Building the Project

To build the project, use the `build` target:

```sh
make build
```

This command will compile the code and package the application using Gradle.

## Running the Application

### Running Locally

To run the application locally:

```sh
make run
```

The application will be accessible at `http://localhost:8080`.

### Running with Docker

You can also run the application and the database using Docker:

```sh
make start-docker
```

To stop the application and remove the containers:

```sh
make stop-docker
```

## Running Tests

### Running Locally

To run tests locally, use the following command:

```sh
make test
```

### Running Tests in Docker

To run tests inside a Docker container, use:

```sh
make run-docker-tests
```

This command will spin up the necessary containers and run the test suite inside them.

## Makefile Targets

- **`make build`**: Compiles and packages the application.
- **`make test`**: Runs the test suite.
- **`make run`**: Runs the application locally.
- **`make clean`**: Cleans up the build artifacts.
- **`make rebuild`**: Cleans and rebuilds the project.
- **`make env`**: Prints the environment variables.
- **`make docker-build`**: Builds Docker images for the application.
- **`make start-db`**: Starts the database container.
- **`make start-docker`**: Starts the application and database containers.
- **`make run-docker-tests`**: Runs tests inside a Docker container.
- **`make stop-docker`**: Stops and removes Docker containers.

## Deploy Jenkins on AWS

1. See [Jenkins on AWS](https://www.jenkins.io/doc/tutorials/tutorial-for-installing-jenkins-on-AWS/)
    1. Install git and clone this repository
        ```sh
       sudo yum update -y
       sudo yum install git -y
       git clone https://github.com/joao-prg/recipe_management_system.git
        ```
    2. Install docker, docker-compose, add ec2-user to the docker group and start docker
       ```sh
       sudo amazon-linux-extras install docker 
       sudo yum install docker -y
       sudo curl -L https://github.com/docker/compose/releases/latest/download/docker-compose-$(uname -s)-$(uname -m) -o /usr/local/bin/docker-compose
       sudo chmod +x /usr/local/bin/docker-compose
       sudo usermod -a -G docker ec2-user
       newgrp docker
       sudo service docker start
        ```
    3. Instead of installing Jenkins locally, launch it with docker
       ```sh
        docker-compose -f docker-compose-jenkins.yml up docker jenkins
        ```
2. Once Jenkins is up and running:
   1. Make sure the following plugins are installed (Manage Jenkins -> Plugins):
      1. GitHub API Plugin
      2. GitHub Branch Source Plugin
      3. GitHub Integration Plugin
      4. GitHub plugin
      5. Pipeline GitHub Notify Step Plugin
      6. Pipeline: GitHub Groovy Libraries
   2. Add new credentials (Manage Jenkins -> Credentials -> System -> Global credentials unrestricted -> Add credentials)
      1. Kind->Username and password, with the github username and a Personal Access Token as password
      2. Make sure the token has permissions for repo, admin:repo_hook, read:org, and workflow
      3. Copy the credentials ID and set GITHUB_CREDENTIALS_ID in the Jenkinsfile with it
   3. Create a new multibranch pipeline (Create a job -> Multibranch Pipeline)
      1. Point to this repository (Branch Sources -> Github)
      2. Set credentials to the ones created in the previous step
3. Create Github Webhook
   1. Go to the repo page -> Settings -> Webhooks -> Add webhook
   2. Set Payload URL to http://<jenkins-url>:<jenkins_port>/github-webhook/
   3. Set Content type to "application/json"
   4. Check "Just the push event" or edit which events will trigger a build

## License

This project is released into the public domain under [The Unlicense](LICENSE). This means you can freely use, modify, and distribute the code with no restrictions. For more details, see the [LICENSE](LICENSE) file.
