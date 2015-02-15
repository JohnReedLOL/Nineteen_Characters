package src.view;

import java.util.Scanner;

import src.controller.Avatar;
import src.controller.Item;
import src.controller.Terrain;
import src.model.MapMain_Relation;

public class TestingMain {
    public TestingMain() {
        // TODO Auto-generated constructor stub
    }

    /**
     * @param args
     */
    public static void printArray(Viewport view) {
        char[][] in = view.getContents();
        for (int j = 0; j != view.height_; ++j) {
            for (int i = 0; i != view.width_; ++i) {
                {
                    System.out.print(in[i][j]);
                }
            }
            System.out.println();
        }
    }

    public static void oldtest() {
        MapMain_Relation mmr_ = new MapMain_Relation();
        mmr_.bindToNewMapOfSize(Viewport.width_, Viewport.height_); //Can change these later if we so desire. 
        Avatar avatar = new Avatar("avatar", 'x', 0, 0);
        avatar.setMap(mmr_);
        ViewPortTester Tester = new ViewPortTester();
        printArray(Tester);
        System.out.println("Done with viewportTester, ccviewtime!");
        Display _display = new Display(new AvatarCreationView(avatar));
        _display.printView();
    }

    public static void main(String[] args) {
        MapMain_Relation mmr_ = new MapMain_Relation();
        mmr_.bindToNewMapOfSize(Viewport.width_ / 2, Viewport.height_); //Can change these later if we so desire. 
        Avatar avatar = new Avatar("avatar", '☉', 0, 0);
        avatar.setMap(mmr_);
        Item equipable = new Item("☂", '☂', true, true, false);

        // ▨
        for (int y = 0; y < Viewport.height_; ++y) {
            for (int x = 0; x < Viewport.width_ / 2; ++x) {
                Terrain obstacle = new Terrain("land", '▨', false, false);
                mmr_.addTerrain(obstacle, x, y);
            }
        }
        mmr_.addItem(equipable, 5, 5);
        Display _display = new Display(avatar.getMyView());
        _display.printView();
        Terrain obstacle = new Terrain("boulder", '☠', true, false);
        mmr_.addTerrain(obstacle, 2, 2);
        System.out.println("☠ and ★ and ✚");
        Display.setMessage("test",3);
        Scanner sc = new Scanner(System.in);
        while (true) {
            char c;
            c = sc.next().charAt(0);
            if (c == '5') {
                return;
            }
            avatar.getInput(c);
            _display.setView(avatar.getMyView());
            _display.printView();

        }

    }

}
