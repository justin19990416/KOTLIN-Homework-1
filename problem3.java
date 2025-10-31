package com.example.prob3; // 檔案實際所在套件；需與 Gradle namespace 對齊，否則 R 會找不到

import android.os.Bundle; // 匯入 Bundle 型別，onCreate 會用到
import android.view.View; // 匯入 View，取得根視圖與調整 padding
import android.widget.Button; // 匯入 Button 控制項
import android.widget.EditText; // 匯入 EditText 控制項
import android.widget.RadioGroup; // 匯入 RadioGroup 控制項
import android.widget.TextView; // 匯入 TextView 控制項

import androidx.annotation.Nullable; // 匯入 Nullable 註解，標示參數可為空
import androidx.appcompat.app.AppCompatActivity; // 匯入 AppCompatActivity 基底類
import androidx.core.graphics.Insets; // 匯入 Insets 結構，用來取得四邊 inset 數值
import androidx.core.view.ViewCompat; // 匯入 ViewCompat，處理 WindowInsets 相關工具
import androidx.core.view.WindowCompat; // 匯入 WindowCompat，設定 edge-to-edge 行為
import androidx.core.view.WindowInsetsCompat; // 匯入 WindowInsetsCompat，取得系統列/鍵盤 Insets

import java.util.concurrent.ThreadLocalRandom; // 匯入 ThreadLocalRandom，用於產生電腦出拳的亂數

import com.example.prob3.R; // 明確匯入本 app 的 R（避免找錯命名空間）

public class MainActivity extends AppCompatActivity { // 宣告主畫面的 Activity 類別（Java：extends AppCompatActivity）

    @Override // 標示覆寫父類別方法
    protected void onCreate(@Nullable Bundle savedInstanceState) { // 覆寫 onCreate：Activity 建立時呼叫；@Nullable 表示可為空
        super.onCreate(savedInstanceState); // 呼叫父類別初始化
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false); // 啟用 edge-to-edge：內容可延伸到狀態列/導覽列下方（Java 等價作法）

        setContentView(R.layout.activity_main); // 指定版面配置檔（需存在 res/layout/activity_main.xml）

        final View root = findViewById(R.id.main); // 取得根容器（XML 的 @+id/main）
        final InitialPadding rootInitial = recordInitialPadding(root); // 紀錄原始 padding，避免之後覆蓋掉原有間距

        ViewCompat.setOnApplyWindowInsetsListener(root, (v, insets) -> { // 設置 Insets 監聽器：系統列/鍵盤變化時回調
            Insets sysBars = insets.getInsets(WindowInsetsCompat.Type.systemBars()); // 取得系統列（狀態列+導覽列）的各邊距
            int mask = WindowInsetsCompat.Type.navigationBars() | WindowInsetsCompat.Type.ime(); // 建立「底部導覽列或鍵盤」的型別遮罩（位元或）
            Insets bottomNavIme = insets.getInsets(mask); // 取得底部導覽列或鍵盤（IME）目前可見的 Insets

            v.setPadding( // 以「原始 padding + Insets」的方式疊加，避免覆寫原有間距
                    rootInitial.left + sysBars.left, // 左側 padding = 原始左 + 系統列左
                    rootInitial.top + sysBars.top, // 上側 padding = 原始上 + 狀態列高度（含瀏海）
                    rootInitial.right + sysBars.right, // 右側 padding = 原始右 + 系統列右
                    rootInitial.bottom + bottomNavIme.bottom // 下側 padding = 原始下 + 底部導覽列或鍵盤高度
            ); // 套用新的四邊 padding
            return insets; // 回傳 insets 讓子 View 仍可進一步處理（若有需要）
        }); // Insets 監聽器設置完成

        ViewCompat.requestApplyInsets(root); // 主動請求套用一次 insets，避免必須等互動才更新

        final EditText edName = findViewById(R.id.edName); // 取得姓名輸入框
        final TextView tvText = findViewById(R.id.tvText); // 取得提示文字
        final RadioGroup radioGroup = findViewById(R.id.radioGroup); // 取得出拳選項群組
        final Button btnMora = findViewById(R.id.btnMora); // 取得猜拳按鈕
        final TextView tvName = findViewById(R.id.tvName); // 取得名字顯示欄
        final TextView tvWinner = findViewById(R.id.tvWinner); // 取得勝利者顯示欄
        final TextView tvMyMora = findViewById(R.id.tvMyMora); // 取得我方出拳顯示欄
        final TextView tvTargetMora = findViewById(R.id.tvTargetMora); // 取得電腦出拳顯示欄

        btnMora.setOnClickListener(v -> { // 綁定按鈕點擊事件（Java 8 Lambda；亦可用匿名類別）
            String playerName = edName.getText() != null ? edName.getText().toString().trim() : ""; // 讀取姓名並去除前後空白（防止 NPE）
            if (playerName.isEmpty()) { // 檢查是否為空字串
                edName.setError("請輸入玩家姓名"); // 在輸入框上顯示錯誤提示
                tvText.setText("請輸入玩家姓名"); // 同步更新提示文字
                return; // 中止後續流程（Kotlin 的 return@setOnClickListener 在 Java 直接 return）
            } // if 結束

            Mora myMora; // 宣告我方出拳變數
            switch (radioGroup.getCheckedRadioButtonId()) { // 依選取的單選鈕決定我方出拳
                case R.id.btnScissor: myMora = Mora.SCISSORS; break; // 選剪刀
                case R.id.btnStone:   myMora = Mora.ROCK;      break; // 選石頭
                case R.id.btnPaper:   myMora = Mora.PAPER;     break; // 選布
                default:              myMora = Mora.SCISSORS;         // 保底（理論上不會到）
            } // switch 結束

            Mora targetMora = Mora.fromInt(ThreadLocalRandom.current().nextInt(3)); // 產生電腦出拳（0..2）

            tvName.setText("名字\n" + playerName); // 顯示玩家名字（換行顯示）
            tvMyMora.setText("我方出拳\n" + myMora.toLabel()); // 顯示我方出拳（中文標籤）
            tvTargetMora.setText("電腦出拳\n" + targetMora.toLabel()); // 顯示電腦出拳（中文標籤）

            Result result = decideWinner(myMora, targetMora); // 依出拳判斷勝負
            switch (result) { // 根據勝負結果更新 UI
                case DRAW: // 平手
                    tvWinner.setText("勝利者\n平手"); // 顯示平手
                    tvText.setText("平局，請再試一次！"); // 提示再來一局
                    break; // 分支結束
                case PLAYER: // 玩家勝
                    tvWinner.setText("勝利者\n" + playerName); // 顯示玩家為勝利者
                    tvText.setText("恭喜你獲勝了！！！"); // 恭喜訊息
                    break; // 分支結束
                case NPC: // 電腦勝
                    tvWinner.setText("勝利者\n電腦"); // 顯示電腦為勝者
                    tvText.setText("可惜，電腦獲勝了！"); // 鼓勵下次再挑戰
                    break; // 分支結束
            } // switch(result) 結束
        }); // setOnClickListener 結束
    } // onCreate 結束

    // ===== 下方為與邏輯相關的列舉/工具類/方法（對應 Kotlin 的 enum、data class、擴充函式） =====

    private enum Mora { // 列舉：三種出拳
        SCISSORS, // 剪刀
        ROCK,     // 石頭
        PAPER;    // 布

        public static Mora fromInt(int i) { // 靜態工廠方法：將 0/1/2 轉成對應出拳（對應 Kotlin companion object）
            switch (i) { // 依輸入數字回傳對應列舉
                case 0:  return SCISSORS; // 0 → 剪刀
                case 1:  return ROCK;     // 1 → 石頭
                default: return PAPER;    // 其他（含 2）→ 布
            } // switch 結束
        } // fromInt 結束

        public String toLabel() { // 實例方法：列舉轉中文標籤（對應 Kotlin 的擴充函式 Mora.toLabel）
            switch (this) { // 根據當前列舉值回傳中文
                case SCISSORS: return "剪刀"; // SCISSORS → 剪刀
                case ROCK:     return "石頭"; // ROCK → 石頭
                case PAPER:
                default:       return "布";   // PAPER → 布
            } // switch 結束
        } // toLabel 結束
    } // Mora 列舉結束

    private enum Result { // 勝負結果列舉
        DRAW,   // 平手
        PLAYER, // 玩家勝
        NPC     // 電腦勝
    } // Result 列舉結束

    private static Result decideWinner(Mora me, Mora npc) { // 判斷勝負並回傳結果（對應 Kotlin 的 decideWinner 函式）
        if (me == npc) return Result.DRAW; // 同手勢 → 平手
        if ((me == Mora.SCISSORS && npc == Mora.PAPER) || // 剪刀勝布
            (me == Mora.ROCK     && npc == Mora.SCISSORS) || // 石頭勝剪刀
            (me == Mora.PAPER    && npc == Mora.ROCK)) { // 布勝石頭
            return Result.PLAYER; // 符合上述任一條件 → 玩家勝
        } // if 結束
        return Result.NPC; // 其餘 → 電腦勝
    } // decideWinner 結束

    private static final class InitialPadding { // 小資料類：封裝四邊 padding（對應 Kotlin data class InitialPadding）
        final int left;   // 左 padding
        final int top;    // 上 padding
        final int right;  // 右 padding
        final int bottom; // 下 padding

        InitialPadding(int left, int top, int right, int bottom) { // 建構子：指定四邊
            this.left = left;   // 指派左 padding
            this.top = top;     // 指派上 padding
            this.right = right; // 指派右 padding
            this.bottom = bottom; // 指派下 padding
        } // 建構子結束
    } // InitialPadding 類別結束

    private static InitialPadding recordInitialPadding(View v) { // 靜態工具方法：讀取當前 View 的四邊 padding（對應 Kotlin 擴充函式）
        return new InitialPadding( // 建立並回傳封裝物件
                v.getPaddingLeft(), // 讀取左 padding
                v.getPaddingTop(), // 讀取上 padding
                v.getPaddingRight(), // 讀取右 padding
                v.getPaddingBottom() // 讀取下 padding
        ); // 回傳 InitialPadding
    } // recordInitialPadding 結束
} // MainActivity 類別結束
