package ai.fitme.ayahupgrade.bean;

public class ModelConfig {

    private String intent;
    private int index;
    private String type;
    private float ambiguous_score;
    private float optimal_score;

    public String getIntent() {
        return intent;
    }

    public void setIntent(String intent) {
        this.intent = intent;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public float getAmbiguous_score() {
        return ambiguous_score;
    }

    public void setAmbiguous_score(float ambiguous_score) {
        this.ambiguous_score = ambiguous_score;
    }

    public float getOptimal_score() {
        return optimal_score;
    }

    public void setOptimal_score(float optimal_score) {
        this.optimal_score = optimal_score;
    }
}
