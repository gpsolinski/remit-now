package com.gpsolinski.remitnow

import groovyx.net.http.HttpResponseException

class TransferApiTest extends ApiTestBase {

  def 'transfer from account with sufficient balance'() {
    given: 'an account with sufficient balance'
    def sender = createUSDAccount()
    createDeposit(sender.id, 100.00)

    and: 'an existing recipient account'
    def recipient = createUSDAccount()

    and: 'a valid transfer amount'
    def amountToTransfer = 50.00

    when:
    def response = client.post(path: "/transfer",
        body: "{\"fromAccount\" : ${sender.id}, \"toAccount\": ${recipient.id},\"amount\": \"${amountToTransfer}\"}"
    )

    then:
    with (response) {
      status == 200
      with (data) {
        id >= 1
        fromAccount == sender.id
        toAccount == recipient.id
        amount == amountToTransfer.toPlainString()
        state == 'COMPLETED'
        type == 'TRANSFER'
      }
    }
  }

  def 'transfer from account with insufficient balance'() {
    given: 'an account with zero balance'
    def sender = createUSDAccount()

    and: 'an existing recipient account'
    def recipient = createUSDAccount()

    and: 'an excessive transfer amount'
    def amountToTransfer = 50.00

    when:
    client.post(path: "/transfer",
        body: "{\"fromAccount\" : ${sender.id}, \"toAccount\": ${recipient.id},\"amount\": \"${amountToTransfer}\"}"
    )

    then:
    def e = thrown(HttpResponseException)
    with (e.response) {
      status == 400
      with (data) {
        message == "Insufficient balance in the account with ID ${sender.id}"
      }
    }
  }
}
