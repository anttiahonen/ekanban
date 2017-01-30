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
    @game_service = GameService.new(@gameInitService, @playerService, @gameOptionService, @gameRepository)
  end

  describe 'startGame()' do

    let(:blankGame) {GameBuilder.aGame().build()}

    context 'with normal difficulty' do

      before(:each) do
        Mockito.when(@gameInitService.getInitializedGame(
            Mockito.any(GameDifficulty.java_class), Mockito.anyString)).thenReturn(blankGame)
        Mockito.when(@gameRepository.save(blankGame)).thenReturn(blankGame)
      end

      subject { @game_service.start_game("name", GameDifficulty::NORMAL) }

      it 'should return new game' do
        expect(subject).to eq blankGame
      end
      
    end
    
  end

  describe 'exception checking' do

    context 'with throwError()' do

      subject { @game_service.throw_error }

      it 'works like a charm' do
        expect { subject }.to raise_error(Java::JavaLang::RuntimeException, 'exception')
      end

    end

  end

end