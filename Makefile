GRADLE = ./gradlew
PROJECT_NAME = recipe_management_system
SRC_DIR = src/main/java
TEST_DIR = src/test/java
BUILD_DIR = build
MAIN_CLASS = com.joaogoncalves.recipes.RecipesApplication

ifneq (,$(wildcard .env))
    include .env
    export $(shell sed 's/=.*//' .env)
endif

.PHONY: build
build:
	$(GRADLE) build

.PHONY: test
test:
	$(GRADLE) test

.PHONY: run
run: start-db
	$(GRADLE) bootRun

.PHONY: clean
clean:
	$(GRADLE) clean

.PHONY: rebuild
rebuild: clean build

.PHONY: env
env:
	@echo $(ADMIN_EMAIL)
	@echo $(ADMIN_PASSWORD)
	@echo $(POSTGRES_USER)
	@echo $(POSTGRES_PASSWORD)

.PHONY: start-db
start-db:
	docker compose up recipes_db -d

.PHONY: start-docker
start-docker:
	docker compose up recipe_management_system recipes_db -d

run-docker-tests:
	docker compose up recipe_management_system_test

.PHONY: stop-docker
stop-docker:
	docker compose down
