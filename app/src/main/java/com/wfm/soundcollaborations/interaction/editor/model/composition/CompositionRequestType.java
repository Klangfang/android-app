package com.wfm.soundcollaborations.interaction.editor.model.composition;

public enum CompositionRequestType {

    CREATE("Congratulations! Your composition has been published!"),
    JOIN("Congratulations! Your sounds has been published!"),
    CANCEL("Ok! Your collaboration has been canceled!"),
    FAILED("Failed to cancel collaboration!");

    private final String text;


    CompositionRequestType(String text) {

        this.text = text;

    }


    public String getText() {

        return text;

    }

}
