package net.danh.mmoshop.Calculator;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Calculator {
    private static boolean isExpresstion(String s) {
        s = s.replaceAll(" ", "");
        boolean is = true;
        for (int i = 0; i < s.length(); i++) {
            if (!String.valueOf(s.charAt(i)).matches("[\\d+\\-*/%^@{}#().]")) {
                is = false;
                break;
            }
        }
        return is;
    }

    private static double parsefirst(String s) {
        s = s.replaceAll(" ", "");
        if (s.startsWith("@")) {
            int sqrt = 0;
            int open = 0;
            int close = 0;
            double result = 0;
            for (int i = 0; i < s.length(); i++) {
                if (s.charAt(i) == '@') {
                    sqrt = i;
                    break;
                }
            }
            for (int i = 0; i < s.length(); i++) {
                if (s.charAt(i) == '{') {
                    open = i;
                    break;
                }
            }
            for (int i = s.length() - 1; i >= 0; i--) {
                if (s.charAt(i) == '}') {
                    close = i;
                    break;
                }
            }
            if (s.charAt(sqrt + 1) == '{') {
                double number = BigDecimal.valueOf(Double.parseDouble(calculator(s.substring(open + 1, close), -1))).doubleValue();
                result = Math.sqrt(number);
            }
            return result;
        } else if (s.contains("^")) {
            List<Integer> pow = new ArrayList<>();
            double result = 0;
            for (int i = 0; i < s.length(); i++) {
                if (s.charAt(i) == '^') {
                    pow.add(i);
                }
            }
            for (int i = 0; i < pow.size(); i++) {
                if (i == 0) {
                    double a = BigDecimal.valueOf(Double.parseDouble(calculator(s.substring(0, pow.get(i)), -1))).doubleValue();
                    double b;
                    if (i == pow.size() - 1)
                        b = BigDecimal.valueOf(Double.parseDouble(calculator(s.substring(pow.get(0) + 1), -1))).doubleValue();
                    else
                        b = BigDecimal.valueOf(Double.parseDouble(calculator(s.substring(pow.get(0) + 1, pow.get(1)), -1))).doubleValue();
                    result += Math.pow(a, b);
                } else if (i == pow.size() - 1) {
                    double a = BigDecimal.valueOf(Double.parseDouble(calculator(s.substring(pow.get(i) + 1), -1))).doubleValue();
                    result = Math.pow(result, a);
                } else {
                    double a = BigDecimal.valueOf(Double.parseDouble(calculator(s.substring(pow.get(i) + 1, pow.get(i + 1)), -1))).doubleValue();
                    result = Math.pow(result, a);
                }
            }
            return result;
        } else if (s.contains("#")) {
            List<Integer> percent = new ArrayList<>();
            double result = 0;
            for (int i = 0; i < s.length(); i++) {
                if (s.charAt(i) == '#') {
                    percent.add(i);
                }
            }
            for (int i = 0; i < percent.size(); i++) {
                if (i == 0) {
                    double a = BigDecimal.valueOf(Double.parseDouble(calculator(s.substring(0, percent.get(i)), -1))).doubleValue();
                    double b;
                    if (i == percent.size() - 1)
                        b = BigDecimal.valueOf(Double.parseDouble(calculator(s.substring(percent.get(0) + 1), -1))).doubleValue();
                    else
                        b = BigDecimal.valueOf(Double.parseDouble(calculator(s.substring(percent.get(0) + 1, percent.get(1)), -1))).doubleValue();
                    result += (b / 100) * a;
                } else if (i == percent.size() - 1) {
                    double a = BigDecimal.valueOf(Double.parseDouble(calculator(s.substring(percent.get(i) + 1), -1))).doubleValue();
                    result = (a / 100) * result;
                } else {
                    double a = BigDecimal.valueOf(Double.parseDouble(calculator(s.substring(percent.get(i) + 1, percent.get(i + 1)), -1))).doubleValue();
                    result = (a / 100) * result;
                }
            }
            return result;
        } else {
            return BigDecimal.valueOf(Double.parseDouble(s)).doubleValue();
        }
    }

    private static double parsesecond(String s) {
        s = s.replaceAll(" ", "");
        if (s.contains("*") || s.contains("/") || s.contains("%")) {
            List<Integer> terms = new ArrayList<>();
            double result = 0;
            for (int i = 0; i < s.length(); i++) {
                if (s.charAt(i) == '*' || s.charAt(i) == '/' || s.charAt(i) == '%') {
                    terms.add(i);
                }
            }
            for (int i = 0; i < terms.size(); i++) {
                if (i == 0) {
                    double a = BigDecimal.valueOf(Double.parseDouble(calculator(s.substring(0, terms.get(i)), -1))).doubleValue();
                    double b;
                    if (i == terms.size() - 1)
                        b = BigDecimal.valueOf(Double.parseDouble(calculator(s.substring(terms.get(0) + 1), -1))).doubleValue();
                    else
                        b = BigDecimal.valueOf(Double.parseDouble(calculator(s.substring(terms.get(0) + 1, terms.get(1)), -1))).doubleValue();
                    if (s.charAt(terms.get(i)) == '*') result = a * b;
                    else if (s.charAt(terms.get(i)) == '/') result = a / b;
                    else if (s.charAt(terms.get(i)) == '%') result = a % b;
                } else if (i == terms.size() - 1) {
                    double b = BigDecimal.valueOf(Double.parseDouble(calculator(s.substring(terms.get(i) + 1), -1))).doubleValue();
                    if (s.charAt(terms.get(i)) == '*') result *= b;
                    else if (s.charAt(terms.get(i)) == '/') result /= b;
                    else if (s.charAt(terms.get(i)) == '%') result %= b;
                } else {
                    double b = BigDecimal.valueOf(Double.parseDouble(calculator(s.substring(terms.get(i) + 1, terms.get(i + 1)), -1))).doubleValue();
                    if (s.charAt(terms.get(i)) == '*') result *= b;
                    else if (s.charAt(terms.get(i)) == '/') result /= b;
                    else if (s.charAt(terms.get(i)) == '%') result %= b;
                }
            }
            return result;
        } else {
            return BigDecimal.valueOf(Double.parseDouble(s)).doubleValue();
        }
    }

    private static double parsethird(String s) {
        s = s.replaceAll(" ", "");
        List<Integer> operators = new ArrayList<>();
        double result = 0;
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == '+' || s.charAt(i) == '-') {
                operators.add(i);
            }
        }
        for (int i = 0; i < operators.size(); i++) {
            if (i == 0) {
                double a = BigDecimal.valueOf(Double.parseDouble(calculator(s.substring(0, operators.get(i)), -1))).doubleValue();
                double b;
                if (i == operators.size() - 1)
                    b = BigDecimal.valueOf(Double.parseDouble(calculator(s.substring(operators.get(0) + 1), -1))).doubleValue();
                else
                    b = BigDecimal.valueOf(Double.parseDouble(calculator(s.substring(operators.get(0) + 1, operators.get(1)), -1))).doubleValue();
                if (s.charAt(operators.get(i)) == '+') result = a + b;
                else if (s.charAt(operators.get(i)) == '-') result = a - b;
            } else if (i == operators.size() - 1) {
                double b = BigDecimal.valueOf(Double.parseDouble(calculator(s.substring(operators.get(i) + 1), -1))).doubleValue();
                if (s.charAt(operators.get(i)) == '+') result += b;
                else if (s.charAt(operators.get(i)) == '-') result -= b;
            } else {
                double b = BigDecimal.valueOf(Double.parseDouble(calculator(s.substring(operators.get(i) + 1, operators.get(i + 1)), -1))).doubleValue();
                if (s.charAt(operators.get(i)) == '+') result += b;
                else if (s.charAt(operators.get(i)) == '-') result -= b;
            }
        }
        return result;
    }

    public static String calculator(String Expression, int Demical) {
        if (isExpresstion(Expression)) {
            final boolean parseSecond = !Expression.contains("*") && !Expression.contains("/") && !Expression.contains("%");
            final boolean parseFirst = Expression.contains("^") || Expression.contains("@{") || Expression.contains("#");
            if (Demical <= -1) {
                if (!Expression.contains("(") && !Expression.contains(")")) {
                    if (Expression.contains("@{") && Expression.contains("}")) {
                        Expression = Expression.replaceAll(" ", "");
                        final String regex = "(@\\{)([\\d\\s+\\-*/%^.#]+)(})";
                        final Pattern pattern = Pattern.compile(regex);
                        final Matcher matcher = pattern.matcher(Expression);
                        if (matcher.find()) {
                            if (matcher.group(0).substring(2, matcher.group(0).length() - 1).contains("@{") && matcher.group(0).substring(2, matcher.group(0).length() - 1).contains("}")) {
                                Expression = Expression.replaceAll(matcher.group(0), "@{" + calculator(matcher.group(0).replaceAll("@\\{", "").replaceAll("}", ""), -1) + "}");
                            } else {
                                Expression = Expression.replaceAll(matcher.group(0), String.valueOf(parsefirst(matcher.group(0))));
                            }
                        }
                        return calculator(Expression, Demical);
                    } else {
                        if (Expression.contains("+") || Expression.contains("-")) {
                            return String.valueOf(parsethird(Expression));
                        } else if (parseSecond) {
                            if (parseFirst) {
                                return String.valueOf(parsefirst(Expression));
                            } else {
                                return Expression;
                            }
                        } else {
                            return String.valueOf(parsesecond(Expression));
                        }
                    }
                } else {
                    Expression = Expression.replaceAll(" ", "");
                    final String regex = "([(])([\\d\\s+\\-*/%^@{}.#]+)([)])";
                    final Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
                    final Matcher matcher = pattern.matcher(Expression);
                    if (matcher.find()) {
                        Expression = Expression.replaceFirst(regex, calculator(matcher.group(0).replaceAll("\\(", "").replaceAll("\\)", ""), Demical));
                    }
                    return calculator(Expression, Demical);
                }
            } else {
                if (!Expression.contains("(") && !Expression.contains(")")) {
                    if (Expression.contains("@{") && Expression.contains("}")) {
                        Expression = Expression.replaceAll(" ", "");
                        final String regex = "(@\\{)([\\d\\s+\\-*/%^.#]+)(})";
                        final Pattern pattern = Pattern.compile(regex);
                        final Matcher matcher = pattern.matcher(Expression);
                        if (matcher.find()) {
                            if (matcher.group(0).substring(2, matcher.group(0).length() - 1).contains("@{") && matcher.group(0).substring(2, matcher.group(0).length() - 1).contains("}")) {
                                Expression = Expression.replaceAll(matcher.group(0), "@{" + calculator(matcher.group(0).replaceAll("@\\{", "").replaceAll("}", ""), -1) + "}");
                            } else {
                                Expression = Expression.replaceAll(matcher.group(0), String.valueOf(parsefirst(matcher.group(0))));
                            }
                        }
                        return calculator(Expression, Demical);
                    } else {
                        if (Expression.contains("+") || Expression.contains("-")) {
                            return String.valueOf(parsethird(Expression));
                        } else if (parseSecond) {
                            if (parseFirst) {
                                return String.valueOf(parsefirst(Expression));
                            } else {
                                return Expression;
                            }
                        } else {
                            return String.valueOf(parsesecond(Expression));
                        }
                    }
                } else {
                    Expression = Expression.replaceAll(" ", "");
                    final String regex = "([(])([\\d\\s+\\-*/%^@{}.#]+)([)])";
                    final Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
                    final Matcher matcher = pattern.matcher(Expression);
                    if (matcher.find()) {
                        Expression = Expression.replaceFirst(regex, calculator(matcher.group(0).replaceAll("\\(", "").replaceAll("\\)", ""), Demical));
                    }
                    return calculator(Expression, Demical);
                }
            }
        } else {
            return "Couldn't find any expressions";
        }
    }
}
