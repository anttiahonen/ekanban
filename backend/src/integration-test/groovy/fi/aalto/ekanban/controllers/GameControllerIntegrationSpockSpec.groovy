package fi.aalto.ekanban.controllers

import spock.lang.Unroll

import static groovyx.net.http.ContentType.URLENC

import org.springframework.beans.factory.annotation.Autowired
import groovyx.net.http.RESTClient

import fi.aalto.ekanban.SpringIntegrationSpockTest
import fi.aalto.ekanban.repositories.GameRepository
import fi.aalto.ekanban.enums.GameDifficulty

class GameControllerIntegrationSpockSpec extends SpringIntegrationSpockTest {

    @Autowired
    private GameRepository gameRepository

    def setup() {
        gameRepository.deleteAll()
    }

    @Unroll
    def "/games post with playerName #playerName and gameDifficulty #gameDifficulty" () {

        given: "request to create a new game"
            def client = new RESTClient("http://localhost:"+this.port)
            def params = [playerName: playerName, difficultyLevel: gameDifficulty]

        when: "the request is made"
            def response = client.post(path: "/games",
                    body: params,
                    requestContentType: URLENC
            )

        then: "response should contain status 200 ok"
            response.status == 200

        and: "game should have been persisted to db"
            gameRepository.findAll().size() == 1

        and: "response game should have given playerName"
            response.data.playerName == playerName

        and: "response game should have given gameDifficulty"
            response.data.difficultyLevel == gameDifficulty.toString()

        where:
            playerName | gameDifficulty
            "Playah"   | GameDifficulty.NORMAL
            "Pelaaja"  | GameDifficulty.NORMAL

    }
    
}