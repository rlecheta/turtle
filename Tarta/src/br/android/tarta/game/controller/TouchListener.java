package br.android.tarta.game.controller;


public interface TouchListener {

	public boolean onTouch(int x, int y, boolean press);

	public void release();
}
