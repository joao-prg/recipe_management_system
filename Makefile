GRADLE = ./gradlew
PROJECT_NAME = recipe_management_system

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

.PHONY: docker-build
docker-build:
	docker compose build

.PHONY: start-db
start-db:
	docker compose up recipes_db -d

.PHONY: start-docker
start-docker:
	docker compose up recipe_management_system recipes_db -d

.PHONY: run-docker-tests
run-docker-tests:
	docker compose up recipe_management_system_test

.PHONY: stop-docker
stop-docker:
	docker compose down
