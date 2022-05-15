package thread_coordination;

import java.math.BigInteger;

public class ComplexCalculator {

    public static class ComplexCalculation {
        public BigInteger calculateResult(BigInteger base1, BigInteger power1, BigInteger base2, BigInteger power2) {
            BigInteger result;
        /*
            result = ( base1 ^ power1 ) + (base2 ^ power2).
            Where each calculation in (..) is calculated on a different thread
        */
            PowerCalculatingThread thread1 = new PowerCalculatingThread(base1, power1);
            PowerCalculatingThread thread2 = new PowerCalculatingThread(base2, power2);
            thread1.start();
            thread2.start();
            try {
                thread1.join(2000);
                thread2.join(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            result = thread1.getResult().add(thread2.getResult());
            return result;
        }

        private static class PowerCalculatingThread extends Thread {
            private BigInteger result = BigInteger.ONE;
            private final BigInteger base;
            private final BigInteger power;
            private boolean isFinished = false;

            public PowerCalculatingThread(BigInteger base, BigInteger power) {
                this.base = base;
                this.power = power;
            }

            @Override
            public void run() {
           /*
           Implementing the calculation of result = base ^ power
           */
                this.result = calculate(base, power);
                isFinished = true;
            }

            public BigInteger calculate(BigInteger base, BigInteger power) {
                if (base.compareTo(BigInteger.ZERO) == 0) return BigInteger.ZERO;
                BigInteger temp = BigInteger.ZERO;
                BigInteger res = BigInteger.ONE;
                while (temp.compareTo(power) != 0) {
                    res = res.multiply(base);
                    temp = temp.add(BigInteger.ONE);
                }
                return res;
            }

            public BigInteger getResult() {
                return result;
            }

            public boolean isFinished() {
                return isFinished;
            }
        }
    }
}
