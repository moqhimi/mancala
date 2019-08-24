package com.bol.game.mancala.entities.Enums;


public class BolcomEnums {
	public interface Valued {
		int getValue();
	}

	public enum GameStatus implements Valued {
		START(0),
		CONTINUE(1),
		END(2);

		private int value;

		GameStatus(int value) {
			this.value = value;
		}

		@Override
		public int getValue() {
			return  this.value;
		}
	}

	public enum Player implements Valued {
		player1(0),
		player2(1);
		private int value;

		private Player(int value){
			this.value= value;
		}


		public String getName(){
			return this.name();
		}

		@Override
		public int getValue() {
			return this.value;
		}

		public static String getName(int value){
			if(value== player1.value){
				return player1.getName();
			}else {
				return player2.getName();
			}
		}
	}
}
