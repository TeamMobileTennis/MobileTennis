package de.teammt.mobiletennis.game.ui;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Align;
import de.teammt.mobiletennis.game.MobileTennis;


public class UIBuilder {

    private UIBuilder(){}

    /**
     * Create a Label and return it
     * @param text      The text that will be displayed on the Label
     * @param font      The font of the text
     * @param width     The width of the Label
     * @param height    The height of the Label
     * @param xPos      The x-position of the label
     * @param yPos      The y-position of the label
     * @return label    The label
     */
    public static Label CreateLabel(String text, Label.LabelStyle font, float width, float height, float xPos, float yPos){
        Label label = new Label(text, font);
        label.setAlignment(Align.center);
        label.setSize(width, height);
        label.setPosition(xPos, yPos, Align.center);

        return label;
    }


    /**
     * Create a TextButton and return it
     * @param text      The text displayed on the Button
     * @param font      The font of the text
     * @param width     The width of the button
     * @param height    The height of the button
     * @param xPos      The x-position of the button
     * @param yPos      The y-position of the button
     * @return button   The TextButton
     */
    public static TextButton CreateButton(String text, Label.LabelStyle font, float width, float height, float xPos, float yPos){
        TextButton button = new TextButton(text, MobileTennis.skin, "default");
        button.getLabel().setStyle(font);
        button.setSize(width, height);
        button.setPosition(xPos, yPos, Align.center);

        return button;
    }
}
