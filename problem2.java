package com.example.problem2;

import android.graphics.Rect;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Kotlin 頂層函式在 Java 端通常變成 *Kt 類別的 static 方法
        // 例：ActivityKt.enableEdgeToEdge(this);  或  EdgeToEdge.enable(this);
        // 請依你實際引入的 AndroidX 版本替換：
        // ActivityKt.enableEdgeToEdge(this);

        setContentView(R.layout.activity_main);

        final View root = findViewById(R.id.main);
        final Rect rootInitialPadding = recordInitialPadding(root);

        ViewCompat.setOnApplyWindowInsetsListener(root, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            // Java 沒有 Kotlin 的 updatePadding 擴充與命名參數 → 一次把四邊 setPadding
            v.setPadding(
                rootInitialPadding.left   + systemBars.left,
                rootInitialPadding.top    + systemBars.top,
                rootInitialPadding.right  + systemBars.right,
                rootInitialPadding.bottom + systemBars.bottom
            );
            return insets; // 保留傳遞給子 View 的機會
        });

        ViewCompat.requestApplyInsets(root);
    }

    // Kotlin 的擴充函式：private fun View.recordInitialPadding(): Rect = Rect(...)
    // 在 Java 以 private static 工具方法呈現
    private static Rect recordInitialPadding(View v) {
        return new Rect(v.getPaddingLeft(), v.getPaddingTop(), v.getPaddingRight(), v.getPaddingBottom());
    }

    // --- 下面是 Kotlin 註解區示意的 IME 上推版本（Java 寫法參考） ---
    /*
    private void setupListInsets(final View list) {
        final Rect listInitialPadding = recordInitialPadding(list);
        ViewCompat.setOnApplyWindowInsetsListener(list, (v, insets) -> {
            int mask = WindowInsetsCompat.Type.navigationBars() | WindowInsetsCompat.Type.ime();
            Insets navIme = insets.getInsets(mask);
            v.setPadding(
                v.getPaddingLeft(),
                v.getPaddingTop(),
                v.getPaddingRight(),
                listInitialPadding.bottom + navIme.bottom
            );
            return insets;
        });
    }
    */
}
