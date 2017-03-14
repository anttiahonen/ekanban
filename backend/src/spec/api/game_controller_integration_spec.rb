require 'java'
require 'airborne'
require_relative '../spec_helper'
require_relative '../spring_context'

describe '/games post' do

  before(:all) do
    @ctx = SpringContext.instance.spring_ctx
    @port = SpringContext.instance.spring_port
  end
  
  start_game_params = [
      {playerName: "pelaaja", gameDifficulty: "NORMAL"},
      {playerName: "playah", gameDifficulty: "NORMAL"}
  ]

  start_game_params.each do |start|
    player_name = start[:playerName]
    game_difficulty = start[:gameDifficulty]
    context "when creating with playerName as #{player_name} and difficulty as #{game_difficulty}" do

        before(:all) do
          post 'http://localhost:'+@port.to_s+'/games', { },
               { params: { playerName: player_name, difficultyLevel: game_difficulty } }
        end

        let(:gameRepository) { @ctx.getBean "gameRepository" }

        it 'should insert to db' do
          expect(gameRepository.findAll.size).to eq 1
        end

        it 'should return 200 ok' do
          expect_status 200
        end

        it "should set given playerName: #{player_name} for game" do
          expect(json_body[:playerName]).to eq(player_name)
        end

        it "should create game with given #{game_difficulty} difficulty level" do
          expect(json_body[:difficultyLevel]).to eq(game_difficulty)
        end
    end
  end

end