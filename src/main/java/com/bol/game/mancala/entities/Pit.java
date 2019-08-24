package com.bol.game.mancala.entities;


public class Pit {

	private int pit1;
	private int pit2;
	private int pit3;
	private int pit4;
	private int pit5;
	private int pit6;

	public Pit(int pit1, int pit2, int pit3, int pit4, int pit5, int pit6) {
		this.pit1 = pit1;
		this.pit2 = pit2;
		this.pit3 = pit3;
		this.pit4 = pit4;
		this.pit5 = pit5;
		this.pit6 = pit6;
	}

	public Pit() {
	}

	public int getPit1() {
		return pit1;
	}

	public void setPit1(int pit1) {
		this.pit1 = pit1;
	}

	public int getPit2() {
		return pit2;
	}

	public void setPit2(int pit2) {
		this.pit2 = pit2;
	}

	public int getPit3() {
		return pit3;
	}

	public void setPit3(int pit3) {
		this.pit3 = pit3;
	}

	public int getPit4() {
		return pit4;
	}

	public void setPit4(int pit4) {
		this.pit4 = pit4;
	}

	public int getPit5() {
		return pit5;
	}

	public void setPit5(int pit5) {
		this.pit5 = pit5;
	}

	public int getPit6() {
		return pit6;
	}

	public void setPit6(int pit6) {
		this.pit6 = pit6;
	}

	@Override public String toString() {
		return "Pit{" + "pit1=" + pit1 + ", pit2=" + pit2 + ", pit3=" + pit3 + ", pit4=" + pit4 + ", pit5=" + pit5 + ", pit6=" + pit6 + '}';
	}
}
