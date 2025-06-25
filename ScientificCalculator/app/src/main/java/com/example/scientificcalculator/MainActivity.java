package com.example.scientificcalculator;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import net.objecthunter.exp4j.function.Function;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    EditText editTextInput;
    double lastAnswer = 0;
    boolean isDegree = true;
    Map<Integer, String> inputMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextInput = findViewById(R.id.editTextInput);
        initializeInputMap();

        for (int id : inputMap.keySet()) {
            Button b = findViewById(id);
            b.setOnClickListener(this);
        }

        findViewById(R.id.btnEqual).setOnClickListener(v -> evaluateExpression());
        findViewById(R.id.btnClear).setOnClickListener(v -> clear());
        findViewById(R.id.btnBackspace).setOnClickListener(v -> backspace());
        findViewById(R.id.btnDegRad).setOnClickListener(v -> switchDegRad());
        findViewById(R.id.btnPrev).setOnClickListener(v -> moveCursor(-1));
        findViewById(R.id.btnNext).setOnClickListener(v -> moveCursor(1));
    }

    private void initializeInputMap() {
        inputMap = new HashMap<>();

        // Digits
        inputMap.put(R.id.btn0, "0");
        inputMap.put(R.id.btn1, "1");
        inputMap.put(R.id.btn2, "2");
        inputMap.put(R.id.btn3, "3");
        inputMap.put(R.id.btn4, "4");
        inputMap.put(R.id.btn5, "5");
        inputMap.put(R.id.btn6, "6");
        inputMap.put(R.id.btn7, "7");
        inputMap.put(R.id.btn8, "8");
        inputMap.put(R.id.btn9, "9");

        // Operators
        inputMap.put(R.id.btnPlus, "+");
        inputMap.put(R.id.btnMinus, "-");
        inputMap.put(R.id.btnMultiply, "*");
        inputMap.put(R.id.btnDivide, "/");
        inputMap.put(R.id.btnDot, ".");
        inputMap.put(R.id.btnPower, "^");
        inputMap.put(R.id.btnFact, "!");
        inputMap.put(R.id.btnOpenParen, "(");
        inputMap.put(R.id.btnCloseParen, ")");

        // Constants
        inputMap.put(R.id.btnPi, String.valueOf(Math.PI));
        inputMap.put(R.id.btnE, String.valueOf(Math.E));
        inputMap.put(R.id.btnAns, "Ans");

        // Functions
        inputMap.put(R.id.btnSqrt, "sqrt(");
        inputMap.put(R.id.btnLn, "log("); // natural log
        inputMap.put(R.id.btnLog, "log10(");
        inputMap.put(R.id.btnAbs, "abs(");
        inputMap.put(R.id.btnExp, "exp(");
        inputMap.put(R.id.btnMod, "%");
        inputMap.put(R.id.btnTenPowX, "10^");
        inputMap.put(R.id.btnExpX, "e^");

        // Trigonometric
        inputMap.put(R.id.btnSin, "sin(");
        inputMap.put(R.id.btnCos, "cos(");
        inputMap.put(R.id.btnTan, "tan(");
        inputMap.put(R.id.btnSec, "1/cos(");
        inputMap.put(R.id.btnCosec, "1/sin(");
        inputMap.put(R.id.btnCot, "1/tan(");

        // Inverse Trigonometric
        inputMap.put(R.id.btnSinInv, "asin(");
        inputMap.put(R.id.btnCosInv, "acos(");
        inputMap.put(R.id.btnTanInv, "atan(");
    }

    @Override
    public void onClick(View view) {
        String input = inputMap.get(view.getId());
        if (input != null) {
            int pos = editTextInput.getSelectionStart();
            editTextInput.getText().insert(pos, input);
        }
    }

    private void clear() {
        editTextInput.setText("");
    }

    private void backspace() {
        int cursorPos = editTextInput.getSelectionStart();
        if (cursorPos > 0) {
            editTextInput.getText().delete(cursorPos - 1, cursorPos);
        }
    }

    private void moveCursor(int direction) {
        int pos = editTextInput.getSelectionStart();
        int newPos = Math.max(0, Math.min(pos + direction, editTextInput.getText().length()));
        editTextInput.setSelection(newPos);
    }

    private void switchDegRad() {
        isDegree = !isDegree;
        Toast.makeText(this, isDegree ? "Degree Mode" : "Radian Mode", Toast.LENGTH_SHORT).show();
    }

    private void evaluateExpression() {
        String input = editTextInput.getText().toString().replace("Ans", String.valueOf(lastAnswer));

        try {
            Expression expr = new ExpressionBuilder(input)
                    .functions(
                            new Function("sin", 1) {
                                @Override public double apply(double... args) {
                                    return Math.sin(isDegree ? Math.toRadians(args[0]) : args[0]);
                                }
                            },
                            new Function("cos", 1) {
                                @Override public double apply(double... args) {
                                    return Math.cos(isDegree ? Math.toRadians(args[0]) : args[0]);
                                }
                            },
                            new Function("tan", 1) {
                                @Override public double apply(double... args) {
                                    return Math.tan(isDegree ? Math.toRadians(args[0]) : args[0]);
                                }
                            },
                            new Function("asin", 1) {
                                @Override public double apply(double... args) {
                                    return isDegree ? Math.toDegrees(Math.asin(args[0])) : Math.asin(args[0]);
                                }
                            },
                            new Function("acos", 1) {
                                @Override public double apply(double... args) {
                                    return isDegree ? Math.toDegrees(Math.acos(args[0])) : Math.acos(args[0]);
                                }
                            },
                            new Function("atan", 1) {
                                @Override public double apply(double... args) {
                                    return isDegree ? Math.toDegrees(Math.atan(args[0])) : Math.atan(args[0]);
                                }
                            },
                            new Function("fact", 1) {
                                @Override public double apply(double... args) {
                                    int n = (int) args[0];
                                    if (n < 0) throw new IllegalArgumentException("Negative factorial");
                                    double result = 1;
                                    for (int i = 1; i <= n; i++) result *= i;
                                    return result;
                                }
                            }
                    )
                    .operator(new net.objecthunter.exp4j.operator.Operator("!", 1, true, net.objecthunter.exp4j.operator.Operator.PRECEDENCE_POWER + 1) {
                        @Override
                        public double apply(double... args) {
                            int n = (int) args[0];
                            if (n < 0) throw new IllegalArgumentException("Negative factorial");
                            double result = 1;
                            for (int i = 1; i <= n; i++) result *= i;
                            return result;
                        }
                    })
                    .build();

            double result = expr.evaluate();
            lastAnswer = result;

            String standardForm = getStandardTrigForm(input, result);
            if (standardForm != null) {
                editTextInput.setText(standardForm);
            } else {
                editTextInput.setText(String.valueOf(result));
            }

            editTextInput.setSelection(editTextInput.getText().length());

        } catch (Exception e) {
            editTextInput.setText("Error");
        }
    }

    private String getStandardTrigForm(String input, double value) {
        Map<String, String> standardMap = new HashMap<>();
        standardMap.put("sin(30)", "1/2");
        standardMap.put("cos(60)", "1/2");
        standardMap.put("sin(45)", "√2/2");
        standardMap.put("cos(45)", "√2/2");
        standardMap.put("sin(60)", "√3/2");
        standardMap.put("cos(30)", "√3/2");
        standardMap.put("tan(45)", "1");
        standardMap.put("tan(30)", "√3/3");
        standardMap.put("tan(60)", "√3");

        String cleanedInput = input.replaceAll("\\s+", "");

        if (isDegree && standardMap.containsKey(cleanedInput)) {
            return String.format("%s = %.6f ≈ %s", cleanedInput, value, standardMap.get(cleanedInput));
        }

        return null;
    }
}
