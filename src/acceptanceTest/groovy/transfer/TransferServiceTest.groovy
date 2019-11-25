package transfer

import groovyx.net.http.RESTClient
import spock.lang.Shared
import spock.lang.Specification

class TransferServiceTest extends Specification {

  static final String SERVER_URL = System.getenv("SERVER_URL")
  static final String SERVER_PORT = System.getenv("SERVER_PORT")

  @Shared
  def client = new RESTClient("$SERVER_URL:$SERVER_PORT")

  def 'should return 200 when transfer successful'() {
    given: 'an account with sufficient funds'
    def account = 'new account'

    when: 'transferring from an account with sufficient funds'
    def response = client.post(path: "account/${accountNo}/transfer",
        body: "{\"to\": \"${targetAccount}\",\"amount\": \"${amount}\"}"
    )

    then:
    assert response.status == 200 : 'response code should be 200 when transfer is valid'
  }
}
