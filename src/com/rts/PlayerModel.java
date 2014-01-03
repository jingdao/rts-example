package com.rts;

public class PlayerModel {

	public int currentHP;
	public int currentMana;
	public int maxHP;
	public int maxMana;
	public int moveSpeed;
	
	public PlayerModel(int maxHP, int maxMana, int moveSpeed) {
		this.maxHP=maxHP;
		this.maxMana=maxMana;
		this.currentHP=maxHP;
		this.currentMana=maxMana;
		this.moveSpeed=moveSpeed;
	}

}