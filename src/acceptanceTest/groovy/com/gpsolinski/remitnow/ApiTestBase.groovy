/*
 * Copyright (c) 2019 Grzegorz Solinski. All rights reserved.
 */

package com.gpsolinski.remitnow

import com.gpsolinski.remitnow.web.TransferServiceMainVerticle
import groovyx.net.http.RESTClient
import io.vertx.core.Vertx
import spock.lang.Shared
import spock.lang.Specification

abstract class ApiTestBase extends Specification {

  static final String SERVER_URL = 'localhost'
  static final String SERVER_PORT = '8080'

  @Shared
  def client = new RESTClient("http://$SERVER_URL:$SERVER_PORT", "application/json")


  @Shared Vertx vertx
  @Shared String verticleId

  def setupSpec() {
    vertx = Vertx.vertx()
    vertx.deployVerticle(TransferServiceMainVerticle.class.getName(), { deployResponse ->
      assert deployResponse.succeeded()
      verticleId = deployResponse.result()
    })
    Thread.sleep 1000
  }

  def cleanupSpec() {
    Thread.sleep 2000
    vertx.undeploy(verticleId) { response ->
      assert response.succeeded()
      vertx.close()
    }
  }

  def createUSDAccount() {
    def requestBody = '{"currency": "USD"}'
    def response = client.post(path: '/accounts', body: requestBody)
    assert response.status == 201
    return response.data
  }

  def createDeposit(Long accountId, BigDecimal amount) {
    def requestBody = "{\"amount\": \"${amount}\"}"
    def response = client.post(path: "/account/${accountId}/deposit", body: requestBody)
    assert response.status == 200
  }
}
