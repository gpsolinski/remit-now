/*
 * Copyright (c) 2019 Grzegorz Solinski. All rights reserved.
 */

package com.gpsolinski.remitnow.validators

import spock.lang.Specification

class TransactionAmountValidatorTest extends Specification {

  def 'pass validation when amount positive'() {
    given: 'a positive amount'
    def amount = 10.00

    when:
    TransactionAmountValidator.validate(amount)

    then:
    noExceptionThrown()
  }

  def 'fail validation'() {
    when:
    TransactionAmountValidator.validate(amount)

    then:
    def e = thrown(IllegalArgumentException)
    e.message == 'Transaction amount needs to be positive'

    where:
    amount << [-10.00, 0.00]
  }
}
