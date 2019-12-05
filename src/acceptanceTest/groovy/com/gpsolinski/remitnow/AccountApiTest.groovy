package com.gpsolinski.remitnow


import groovyx.net.http.HttpResponseException

class AccountApiTest extends ApiTestBase {

  def 'create account'() {
    given: 'request body containing valid currency code'
    def requestBody = '{"currency": "USD"}'

    when:
    def response = client.post(path: '/accounts', body: requestBody)

    then:
    with (response) {
      status == 201
      with (data) {
        Long.valueOf(id) >= 1
        currency == 'USD'
        new BigDecimal(availableBalance) == 0.00
        new BigDecimal(balance) == 0.00
      }
    }
  }

  def 'create account without specifying currency'() {
    given: 'an empty request body'
    def requestBody = '{}'

    when:
    client.post(path: '/accounts', body: requestBody)

    then:
    def e = thrown(HttpResponseException)
    with (e.response) {
      status == 400
      with (data) {
        message == '$.currency: is missing but it is required'
        property == 'body'
        errorType == 'JSON_INVALID'
      }
    }
  }

  def 'create account with wrong format of currency'() {
    given: 'a request body with invalid currency'
    def requestBody = "{\"currency\": \"${currency}\"}"

    when:
    client.post(path: '/accounts', body: requestBody)

    then:
    def e = thrown(HttpResponseException)
    with (e.response) {
      status == 400
      with (data) {
        message == '$.currency: does not match the regex pattern ^[A-Z]{3}$'
        property == 'body.currency'
        errorType == 'JSON_INVALID'
      }
    }

    where:
    currency << ['abc', 'whatever', 'dollar', 'usd']
  }

  def 'create account with unrecognizable currency'() {
    given: 'a request body with invalid currency'
    def requestBody = '{"currency": "BLA"}'

    when:
    client.post(path: '/accounts', body: requestBody)

    then:
    def e = thrown(HttpResponseException)
    with (e.response) {
      status == 400
      with (data) {
        message == 'Currency code was not recognized'
        property == 'body.currency'
        errorType == 'JSON_INVALID'
      }
    }
  }

  def 'get existing account'() {
    given: 'an existing account'
    def account = createUSDAccount()

    when:
    def response = client.get(path: "/account/${account.id}")

    then:
    response.data == account
  }

  def 'get nonexistent account'() {
    given: 'a nonexistent account id'
    def id = 123456

    when:
    client.get(path: "/account/${id}")

    then:
    def e = thrown(HttpResponseException)
    with (e.response) {
      status == 404
      statusLine.reasonPhrase == 'Not found'
      with (data) {
        message == "Account with the given ID does not exist"
      }
    }
  }

  def 'get existing accounts'() {
    given: 'two existing accounts'
    def accountOne = createUSDAccount()
    def accountTwo = createUSDAccount()

    when:
    def response = client.get(path: '/accounts')

    then:
    response.data.containsAll([accountOne, accountTwo])
  }

  def 'deposit some money to an account'() {
    given: 'an existing account'
    def account = createUSDAccount()

    and: 'a request body with valid amount'
    def amount = 50.00
    def body = "{\"amount\": \"${amount}\"}"

    when:
    def response = client.post(path: "/account/${account.id}/deposit", body: body)

    then:
    with (response) {
      status == 200
      with (data) {
        id == account.id
        availableBalance == amount.toPlainString()
      }
    }
  }

  def 'withdraw some money from an account with sufficient funds'() {
    given: 'an existing account'
    def account = createUSDAccount()

    and: 'non-zero balance on the account'
    def initialBalance = 100.00
    def depositResp = client.post(path: "/account/${account.id}/deposit", body: "{\"amount\": \"${initialBalance}\"}")
    assert depositResp.status == 200

    and: 'a request body with valid amount'
    def amount = 50.00
    def body = "{\"amount\": \"${amount}\"}"

    when:
    def response = client.post(path: "/account/${account.id}/withdraw", body: body)

    then:
    with (response) {
      status == 200
      with (data) {
        id == account.id
        availableBalance == (initialBalance - amount).toPlainString()
      }
    }
  }

  def 'withdraw money from an account with insufficient funds'() {
    given: 'an existing account with zero-balance'
    def account = createUSDAccount()

    and: 'a request body with an excessive amount'
    def amount = 50.00
    def body = "{\"amount\": \"${amount}\"}"

    when:
    client.post(path: "/account/${account.id}/withdraw", body: body)

    then:
    def e = thrown(HttpResponseException)
    with (e.response) {
      status == 400
      with (data) {
        message == "Insufficient balance in the given account"
      }
    }

    and: "the account balance is unchanged"
    def targetAccount = client.get(path: "/account/${account.id}")
    with (targetAccount.data) {
      id == account.id
      availableBalance == 0.00.toPlainString()
    }
  }
}
