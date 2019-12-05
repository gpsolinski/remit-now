/*
 * Copyright (c) 2019 Grzegorz Solinski. All rights reserved.
 */

package com.gpsolinski.remitnow

class TransactionApiTest extends ApiTestBase {

    def 'list existing transactions'() {
        given: 'existing account'
        def account = createUSDAccount()

        and: 'some deposits to this account'
        def firstAmount = 34.00
        def secondAmount = 66.00
        createDeposit(account.id, firstAmount)
        createDeposit(account.id, secondAmount)


        when:
        def response = client.get(path: '/transactions')

        then:
        with (response) {
            status == 200
            data.findAll { it.type == 'DEPOSIT' && it.toAccount == account.id}
                .collect { new BigDecimal(it.amount) } == [firstAmount, secondAmount]
        }
    }

    def 'get existing transaction'() {
        given: 'existing account'
        def account = createUSDAccount()

        and: 'some deposits to this account'
        def firstAmount = 34.00
        def secondAmount = 66.00
        createDeposit(account.id, firstAmount)
        createDeposit(account.id, secondAmount)

        def allTransactions = client.get(path: '/transactions')
        with (allTransactions) {
            assert status == 200
            with (data) {
                assert !data.empty
            }
        }

        and:
        def transaction = allTransactions.data.find { new BigDecimal(it.amount) == firstAmount }

        when:
        def retrieved = client.get(path: "/transaction/${transaction.id}")

        then:
        with (retrieved) {
            status == 200
            data == transaction
        }
    }
}
