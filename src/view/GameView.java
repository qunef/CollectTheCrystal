
package view;

import model.Ball;
import model.Basket;
import model.Lasso;
import model.Player;
import java.util.List;

public interface GameView {
    void updateDisplay();
    void setScoreAndCount(int score, int count);
    void setGameElements(Player player, List<Ball> balls, Lasso lasso, Basket basket);
    void showMainMenu();
    void requestGameFocus();
}