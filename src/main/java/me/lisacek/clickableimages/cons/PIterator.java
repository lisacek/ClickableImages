package me.lisacek.clickableimages.cons;

public class PIterator {

    private final String player;

    private int page = 0;

    public PIterator(String player) {
        this.player = player;
    }

    public int getPage() {
        return page;
    }

    public String getPlayer() {
        return player;
    }

    public int getPage(int page) {
        return page;
    }

    public void nextPage() {
        page++;
    }

    public void prevPage() {
        page--;
    }
}
