/*
 * Copyright (c) 2019 Grzegorz Solinski. All rights reserved.
 */

package com.gpsolinski.remitnow.util

import com.gpsolinski.remitnow.domain.impl.TransactionType
import com.gpsolinski.remitnow.domain.impl.accounts.SimpleBankAccount
import com.gpsolinski.remitnow.domain.impl.transactions.FakeTransaction
import com.gpsolinski.remitnow.web.util.JsonUtil
import groovy.json.JsonSlurper
import io.vertx.ext.web.api.validation.ValidationException
import spock.lang.Specification

class JsonUtilTest extends Specification {

  def 'transform account to JsonObject'() {
    given:
    def account = new SimpleBankAccount(1, 20.00)

    when:
    def result = JsonUtil.transformAccount(account)

    then:
    new JsonSlurper().parseText(result.toString()) == [id: 1, currency: "USD", balance: "20.00", availableBalance: "20.00"]
  }

  def 'transform list of accounts to JsonArray'() {
    given:
    def firstAccount = new SimpleBankAccount(1, 20.00)
    def secondAccount = new SimpleBankAccount(2, 30.00)
    def accounts = [firstAccount, secondAccount]

    when:
    def result = JsonUtil.transformAccounts(accounts)

    then:
    new JsonSlurper().parseText(result.toString()) == [[id: 1, currency: "USD", balance: "20.00", availableBalance: "20.00"],
                                                       [id: 2, currency: "USD", balance: "30.00", availableBalance: "30.00"]]
  }

  def 'transform transaction to JsonObject'() {
    given:
    def fromAccount = new SimpleBankAccount(1, 30.00)
    def toAccount = new SimpleBankAccount(2)
    def transaction = new FakeTransaction(1, 20.00, fromAccount, toAccount, TransactionType.TRANSFER)

    when:
    def result = JsonUtil.transformTransaction(transaction)

    then:
    new JsonSlurper().parseText(result.toString()) == [id         : 1,
                                                       fromAccount: fromAccount.id,
                                                       toAccount  : toAccount.id,
                                                       amount     : "20.00",
                                                       state      : "NEW",
                                                       type       : "TRANSFER"]
  }

  def 'transform list of transactions to JsonArray'() {
    given:
    def firstAccount = new SimpleBankAccount(3)
    def secondAccount = new SimpleBankAccount(4)
    def firstTransaction = new FakeTransaction(1, 20.00, firstAccount, null, TransactionType.WITHDRAWAL)
    def secondTransaction = new FakeTransaction(2, 30.00, null, secondAccount, TransactionType.DEPOSIT)
    def transactions = [firstTransaction, secondTransaction]

    when:
    def result = JsonUtil.transformTransactions(transactions)

    then:
    new JsonSlurper().parseText(result.toString()) == [[id         : 1,
                                                        fromAccount: firstAccount.id,
                                                        amount     : "20.00",
                                                        state      : "NEW",
                                                        type       : "WITHDRAWAL"],
                                                       [id         : 2,
                                                        toAccount  : secondAccount.id,
                                                        amount     : "30.00",
                                                        state      : "NEW",
                                                        type       : "DEPOSIT"]]
  }

  def 'transform exception into error payload'() {
    given:
    def message = "some message"
    def exception = new Exception(message)

    when:
    def result = JsonUtil.transformError(exception)

    then:
    new JsonSlurper().parseText(result.toString()) == [message: message]
  }

  def 'create error payload'() {
    given:
    def message = "some message"

    when:
    def result = JsonUtil.createError(message)

    then:
    new JsonSlurper().parseText(result.toString()) == [message: message]
  }

  def 'create validation error payload'() {
    given:
    def message = "some message"
    def jsonPath = "some.prop"
    def errorType = ValidationException.ErrorType.JSON_INVALID

    when:
    def result = JsonUtil.createValidationError(message, jsonPath, errorType)

    then:
    new JsonSlurper().parseText(result.toString()) == [message: message, property: jsonPath, errorType: errorType.toString()]
  }
}
