package repository.impl

import domain.Identifiable
import spock.lang.Specification

class BasicInMemoryRepositoryTest extends Specification {

  def 'saves an Identifiable object in data store'() {
    given: 'a hash map'
    def map = new HashMap<Integer, Identifiable<Integer>>()

    and: 'an Identifiable object'
    def id = 5
    def identifiable = new Identifiable<Integer>() {
      @Override
      Integer getId() {
        return id
      }
    }

    and: 'a BasicInMemoryRepository instance with the map as data store'
    def underTest = new BasicInMemoryRepository<Identifiable<Integer>, Integer>(map) {}

    when:
    underTest.save(identifiable)

    then:
    map == [(id): identifiable]
  }

  def 'retrieves an existing Identifiable object by ID'() {
    given: 'an Identifiable object'
    def id = 10;
    def identifiable = new Identifiable<Integer>() {
      @Override
      Integer getId() {
        return id
      }
    }

    and: 'a hash map containing the Identifiable object'
    def map = [(id): identifiable]

    and: 'a BasicInMemoryRepository instance with the map as data store'
    def underTest = new BasicInMemoryRepository<Identifiable<Integer>, Integer>(map) {}

    when:
    def found = underTest.findById(id)

    then:
    found.get() == identifiable
  }
}
