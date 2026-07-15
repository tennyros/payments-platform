COMPOSE ?= docker compose
GRADLEW ?= ./gradlew

.PHONY: up down logs build test clean

up:
	$(COMPOSE) up -d --build

down:
	$(COMPOSE) down --remove-orphans

logs:
	$(COMPOSE) logs -f --tail=200

build:
	$(GRADLEW) build

test:
	$(GRADLEW) test

clean:
	$(GRADLEW) clean
