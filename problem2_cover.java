public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // 同一份 XML

        final EditText nameInput = findViewById(R.id.edName);
        final RadioGroup group   = findViewById(R.id.radioGroup);
        final Button btn         = findViewById(R.id.btnMora);
        final TextView tvName    = findViewById(R.id.tvName);
        final TextView tvWinner  = findViewById(R.id.tvWinner);
        final TextView tvMy      = findViewById(R.id.tvMyMora);
        final TextView tvAi      = findViewById(R.id.tvTargetMora);

        btn.setOnClickListener(v -> {
            String maybeText = nameInput.getText() != null ? nameInput.getText().toString() : null;
            String name = (maybeText != null) ? maybeText.trim() : "";

            String myChoice;
            switch (group.getCheckedRadioButtonId()) {
                case R.id.btnScissor: myChoice = "剪刀"; break;
                case R.id.btnStone:   myChoice = "石頭"; break;
                default:              myChoice = "布";
            }

            String[] choices = new String[] {"剪刀", "石頭", "布"};
            String aiChoice = choices[(int)(Math.random() * choices.length)];

            String winner;
            if (myChoice.equals(aiChoice)) {
                winner = "平手";
            } else if ( (myChoice.equals("剪刀") && aiChoice.equals("布")) ||
                        (myChoice.equals("石頭") && aiChoice.equals("剪刀")) ||
                        (myChoice.equals("布")   && aiChoice.equals("石頭")) ) {
                winner = "玩家";
            } else {
                winner = "電腦";
            }

            tvName.setText(name.isEmpty() ? "名字：—" : "名字：" + name);
            tvMy.setText("我方出拳：" + myChoice);
            tvAi.setText("電腦出拳：" + aiChoice);
            tvWinner.setText("勝利者：" + winner);
        });
    }
}
