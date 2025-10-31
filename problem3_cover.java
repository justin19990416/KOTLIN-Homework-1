package com.example.prob3; // 套件名稱；需與 Gradle namespace 一致，否則 R 會找不到資源

import android.os.Bundle; // 匯入 Bundle 型別，onCreate 生命週期方法會用到
import android.view.View; // 匯入 View，取得根視圖並操作 padding
import android.widget.Button; // 匯入 Button 控制項
import android.widget.EditText; // 匯入 EditText 控制項（輸入玩家姓名）
import android.widget.RadioGroup; // 匯入 RadioGroup 控制項（剪刀石頭布選項）
import android.widget.TextView; // 匯入 TextView 控制項（顯示結果文字）

import androidx.annotation.Nullable; // 匯入 Nullable 註解，標示參數可為空
import androidx.appcompat.app.AppCompatActivity; // 匯入 AppCompatActivity 作為 Activity 基底類
import androidx.core.graphics.Insets; // 匯入 Insets 結構，取得系統列/鍵盤四邊內距
import androidx.core.view.ViewCompat; // 匯入 ViewCompat，設定 WindowInsets 監聽等功能
import androidx.core.view.WindowCompat; // 匯入 WindowCompat，控制 decorFitsSystemWindows（edge-to-edge）
import androidx.core.view.WindowInsetsCompat; // 匯入 WindowInsetsCompat，跨版本存取各種 insets

import java.util.concurrent.ThreadLocalRandom; // 匯入 ThreadLocalRandom，產生 0..2 的亂數用

import com.example.prob3.R; // 明確匯入本專案的 R 類別（避免命名空間誤用）

public class MainActivity extends AppCompatActivity { // 宣告主畫面 Activity，繼承 AppCompatActivity

    @Override // 標示覆寫父類別方法
    protected void onCreate(@Nullable Bundle savedInstanceState) { // onCreate：Activity 建立時進入點
        super.onCreate(savedInstanceState); // 呼叫父類別 onCreate 完成基本初始化
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false); // 啟用 edge-to-edge：讓內容延伸到狀態列/導覽列下方

        setContentView(R.layout.activity_main); // 指定版面配置檔（對應你提供的 ConstraintLayout XML）

        final View root = findViewById(R.id.main); // 取得根容器（XML 的 @+id/main）
        final InitialPadding rootInitial = recordInitialPadding(root); // 紀錄當前四邊 padding，之後疊加系統列/鍵盤用

        ViewCompat.setOnApplyWindowInsetsListener(root, (v, insets) -> { // 設置 WindowInsets 監聽器（系統列/鍵盤變化時回調）
            Insets sysBars = insets.getInsets(WindowInsetsCompat.Type.systemBars()); // 取得狀態列與導覽列的 Insets（上/下/左/右）
            int mask = WindowInsetsCompat.Type.navigationBars() | WindowInsetsCompat.Type.ime(); // 建立底部導覽列或鍵盤的型別遮罩（位元或）
            Insets bottomNavIme = insets.getInsets(mask); // 取得目前可見的底部導覽列或鍵盤的 Insets
            v.setPadding( // 將「原始 padding + 對應 Insets」相加後套用，避免覆蓋原有留白
                    rootInitial.left + sysBars.left, // 左：原始 left + 系統列 left
                    rootInitial.top + sysBars.top, // 上：原始 top + 狀態列高度（含瀏海/劉海）
                    rootInitial.right + sysBars.right, // 右：原始 right + 系統列 right
                    rootInitial.bottom + bottomNavIme.bottom // 下：原始 bottom + 底部導覽列或鍵盤高度
            ); // setPadding 套用完成
            return insets; // 回傳 insets 讓子 View 如需可再進一步處理
        }); // setOnApplyWindowInsetsListener 設定完成

        ViewCompat.requestApplyInsets(root); // 主動要求立即套用一次 Insets（避免等互動才更新）

        final EditText edName = findViewById(R.id.edName); // 取得姓名輸入框
        final TextView tvText = findViewById(R.id.tvText); // 取得提示文字 TextView
        final RadioGroup radioGroup = findViewById(R.id.radioGroup); // 取得出拳選項群組（含三個 RadioButton）
        final Button btnMora = findViewById(R.id.btnMora); // 取得「猜拳」按鈕
        final TextView tvName = findViewById(R.id.tvName); // 取得結果欄：名字
        final TextView tvWinner = findViewById(R.id.tvWinner); // 取得結果欄：勝利者
        final TextView tvMyMora = findViewById(R.id.tvMyMora); // 取得結果欄：我方出拳
        final TextView tvTargetMora = findViewById(R.id.tvTargetMora); // 取得結果欄：電腦出拳

        btnMora.setOnClickListener(v -> { // 綁定按鈕點擊事件（Java 8 lambda；也可用匿名內部類）
            String playerName = edName.getText() != null ? edName.getText().toString().trim() : ""; // 讀取輸入姓名並去除首尾空白，防 NPE
            if (playerName.isEmpty()) { // 若姓名為空字串
                edName.setError("請輸入玩家姓名"); // 在輸入框上顯示錯誤訊息
                tvText.setText("請輸入玩家姓名"); // 同步更新提示文字
                return; // 提早返回，中止後續流程（Kotlin 的 return@setOnClickListener）
            } // if 結束

            Mora myMora; // 宣告我方出拳列舉變數
            switch (radioGroup.getCheckedRadioButtonId()) { // 根據目前勾選的 RadioButton 決定我方出拳
                case R.id.btnScissor: myMora = Mora.SCISSORS; break; // 若選剪刀 → SCISSORS
                case R.id.btnStone:   myMora = Mora.ROCK;      break; // 若選石頭 → ROCK
                case R.id.btnPaper:   myMora = Mora.PAPER;     break; // 若選布   → PAPER
                default:              myMora = Mora.SCISSORS;         // 預設值（理論上不會到）→ SCISSORS
            } // switch 結束

            Mora targetMora = Mora.fromInt(ThreadLocalRandom.current().nextInt(3)); // 產生電腦出拳（0..2）並轉成對應列舉

            tvName.setText("名字\n" + playerName); // 更新名字結果（換行顯示）
            tvMyMora.setText("我方出拳\n" + myMora.toLabel()); // 更新我方出拳（中文標籤）
            tvTargetMora.setText("電腦出拳\n" + targetMora.toLabel()); // 更新電腦出拳（中文標籤）

            Result result = decideWinner(myMora, targetMora); // 依我方/電腦出拳計算勝負
            switch (result) { // 根據勝負結果更新 UI
                case DRAW: // 平手
                    tvWinner.setText("勝利者\n平手"); // 顯示平手
                    tvText.setText("平局，請再試一次！"); // 顯示提示訊息
                    break; // 結束此分支
                case PLAYER: // 玩家勝
                    tvWinner.setText("勝利者\n" + playerName); // 顯示玩家為勝利者
                    tvText.setText("恭喜你獲勝了！！！"); // 恭喜文字
                    break; // 結束此分支
                case NPC: // 電腦勝
                    tvWinner.setText("勝利者\n電腦"); // 顯示電腦為勝者
                    tvText.setText("可惜，電腦獲勝了！"); // 鼓勵下次再挑戰
                    break; // 結束此分支
            } // switch(result) 結束
        }); // setOnClickListener 綁定完成
    } // onCreate 結束

    // ===================== 下方為輔助型別與函式：對應 Kotlin 的 enum/data class/擴充函式 =====================

    private enum Mora { // 列舉：三種出拳
        SCISSORS, // 剪刀
        ROCK,     // 石頭
        PAPER;    // 布

        public static Mora fromInt(int i) { // 靜態工廠方法：將 0/1/2 轉成對應列舉
            switch (i) { // 依輸入的整數回傳對應出拳
                case 0:  return SCISSORS; // 0 → 剪刀
                case 1:  return ROCK;     // 1 → 石頭
                default: return PAPER;    // 其他（含 2）→ 布
            } // switch 結束
        } // fromInt 結束

        public String toLabel() { // 實例方法：回傳此列舉對應的中文標籤（取代 Kotlin 擴充函式）
            switch (this) { // 依自身列舉值回傳中文字
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

    private static Result decideWinner(Mora me, Mora npc) { // 判斷勝負並回傳結果（等價 Kotlin decideWinner）
        if (me == npc) return Result.DRAW; // 若雙方手勢相同 → 平手
        if ((me == Mora.SCISSORS && npc == Mora.PAPER) || // 條件1：剪刀勝布
            (me == Mora.ROCK     && npc == Mora.SCISSORS) || // 條件2：石頭勝剪刀
            (me == Mora.PAPER    && npc == Mora.ROCK)) { // 條件3：布勝石頭
            return Result.PLAYER; // 任一條件成立 → 玩家勝
        } // if 結束
        return Result.NPC; // 其餘情況 → 電腦勝
    } // decideWinner 結束

    private static final class InitialPadding { // 小資料類：封裝四邊 padding（等價 Kotlin data class InitialPadding）
        final int left; // 左 padding
        final int top; // 上 padding
        final int right; // 右 padding
        final int bottom; // 下 padding

        InitialPadding(int left, int top, int right, int bottom) { // 建構子：指定四邊 padding
            this.left = left; // 指派左 padding
            this.top = top; // 指派上 padding
            this.right = right; // 指派右 padding
            this.bottom = bottom; // 指派下 padding
        } // 建構子結束
    } // InitialPadding 類別結束

    private static InitialPadding recordInitialPadding(View v) { // 靜態工具方法：讀取目前 View 的四邊 padding
        return new InitialPadding( // 建立並回傳 InitialPadding 物件
                v.getPaddingLeft(), // 讀取左 padding 數值
                v.getPaddingTop(), // 讀取上 padding 數值
                v.getPaddingRight(), // 讀取右 padding 數值
                v.getPaddingBottom() // 讀取下 padding 數值
        ); // 回傳完成
    } // recordInitialPadding 結束
} // MainActivity 類別結束
