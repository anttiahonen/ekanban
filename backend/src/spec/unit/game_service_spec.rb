require 'java'
require_relative '../spec_helper'

java_import 'org.mockito.Mockito'
java_import 'org.junit.rules.ExpectedException'

java_import 'fi.aalto.ekanban.builders.GameBuilder'
java_import 'fi.aalto.ekanban.services.GameService'
java_import 'fi.aalto.ekanban.services.PlayerService'
java_import 'fi.aalto.ekanban.services.GameOptionService'
java_import 'fi.aalto.ekanban.services.GameInitService'
java_import 'fi.aalto.ekanban.repositories.GameRepository'
java_import 'fi.aalto.ekanban.enums.GameDifficulty'
java_import 'fi.aalto.ekanban.models.db.games.Game'

describe GameService do

  before(:all) do
    @gameInitService = Mockito.mock(GameInitService.java_class)
    @playerService = Mockito.mock(PlayerService.java_class)
    @gameOptionService = Mockito.mock(GameOptionService.java_class)
    @gameRepository = Mockito.mock(GameRepository.java_class)
    @game_service = GameService.new(@gameInitService, @playerService,
                                    @gameOptionService, @gameRepository)
  end

  describe 'startGame()' do
    let(:player_name) {"Player"}
    let(:game_difficulty) {GameDifficulty::NORMAL}
    let(:new_game) {GameBuilder.aGame().
                        with_player_name(player_name).
                        with_difficulty_level(game_difficulty).
                        build()}

    context 'with normal difficulty' do

      before(:each) do
        Mockito.when(@gameInitService.getInitializedGame(
            Mockito.any(GameDifficulty.java_class), Mockito.anyString)).thenReturn(new_game)
        Mockito.when(@gameRepository.save(new_game)).thenReturn(new_game)
      end

      subject(:created_game) { @game_service.start_game(player_name, game_difficulty) }

      it 'should create game with the given name' do
        expect(created_game.player_name).to eq player_name
      end

      it 'should create game with the given game difficulty' do
        expect(created_game.difficulty_level).to eq game_difficulty
      end
      
    end
  end

end