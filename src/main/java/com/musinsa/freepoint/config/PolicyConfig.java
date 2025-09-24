
package com.musinsa.freepoint.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "point.policy")
public class PolicyConfig {
    private long maxAccrualPerTxn = 100000;
    private long maxWalletBalance = 10000000;
    private int defaultExpiryDays = 365;
    private int minExpiryDays = 1;
    private int maxExpiryDays = 1824;

    public long getMaxAccrualPerTxn() { return maxAccrualPerTxn; }
    public void setMaxAccrualPerTxn(long v) { this.maxAccrualPerTxn = v; }

    public long getMaxWalletBalance() { return maxWalletBalance; }
    public void setMaxWalletBalance(long v) { this.maxWalletBalance = v; }

    public int getDefaultExpiryDays() { return defaultExpiryDays; }
    public void setDefaultExpiryDays(int v) { this.defaultExpiryDays = v; }

    public int getMinExpiryDays() { return minExpiryDays; }
    public void setMinExpiryDays(int v) { this.minExpiryDays = v; }

    public int getMaxExpiryDays() { return maxExpiryDays; }
    public void setMaxExpiryDays(int v) { this.maxExpiryDays = v; }
}
