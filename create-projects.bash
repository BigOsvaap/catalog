#!/usr/bin/env bash

mkdir microservices
cd microservices

spring init \
--boot-version=2.6.2 \
--build=gradle \
--language=kotlin \
--packaging=jar \
--name=product-service \
--package-name=com.bigosvaap.microservices.core.product \
--groupId=com.bigosvaap.microservices.core.product \
--dependencies=actuator,webflux \
--version=1.0.0-SNAPSHOT \
product-service

spring init \
--boot-version=2.6.2 \
--build=gradle \
--language=kotlin \
--packaging=jar \
--name=review-service \
--package-name=com.bigosvaap.microservices.core.review \
--groupId=com.bigosvaap.microservices.core.review \
--dependencies=actuator,webflux \
--version=1.0.0-SNAPSHOT \
review-service

spring init \
--boot-version=2.6.2 \
--build=gradle \
--language=kotlin \
--packaging=jar \
--name=recommendation-service \
--package-name=com.bigosvaap.microservices.core.recommendation \
--groupId=com.bigosvaap.microservices.core.recommendation \
--dependencies=actuator,webflux \
--version=1.0.0-SNAPSHOT \
recommendation-service

spring init \
--boot-version=2.6.2 \
--build=gradle \
--language=kotlin \
--packaging=jar \
--name=product-composite-service \
--package-name=com.bigosvaap.microservices.composite.product \
--groupId=com.bigosvaap.microservices.composite.product \
--dependencies=actuator,webflux \
--version=1.0.0-SNAPSHOT \
product-composite-service

cd ..

spring init \
--boot-version=2.6.2 \
--build=gradle \
--language=kotlin \
--packaging=jar \
--name=api \
--package-name=com.bigosvaap.api \
--groupId=com.bigosvaap.api \
--dependencies=webflux \
--version=1.0.0-SNAPSHOT \
api

spring init \
--boot-version=2.6.2 \
--build=gradle \
--language=kotlin \
--packaging=jar \
--name=util \
--package-name=com.bigosvaap.util \
--groupId=com.bigosvaap.util \
--dependencies=webflux \
--version=1.0.0-SNAPSHOT \
util