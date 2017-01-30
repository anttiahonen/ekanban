require 'java'
require 'airborne'
require_relative '../spec_helper'

describe '/games post' do

  before(:all) do
    @ctx = spring_ctx
    @port = spring_port
  end

  after(:all) do
    @ctx.close
  end

  start_game_params = [
      {playerName: "pelaaja", gameDifficulty: "NORMAL"},
      {playerName: "playah", gameDifficulty: "NORMAL"}
  ]

  start_game_params.each do |start|
    playerName = start[:playerName]
    gameDifficulty = start[:gameDifficulty]
    context "when creating with playerName as #{playerName} and difficulty as #{gameDifficulty}" do

        before(:all) do
          post 'http://localhost:'+@port.to_s+'/games', { },
               { params: { playerName: playerName, difficultyLevel: gameDifficulty } }
        end

        let(:gameRepository) { @ctx.getBean "gameRepository" }

        it 'should insert to db' do
          expect(gameRepository.findAll.size).to eq 1
        end

        it "should set given playerName: #{playerName} for game" do
          expect(json_body[:playerName]).to eq(playerName)
        end

        it "should create game with given #{gameDifficulty} difficulty level" do
          expect(json_body[:difficultyLevel]).to eq(gameDifficulty)
        end

    end
  end

end