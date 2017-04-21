package com.antonjohansson.elasticsearchshell.shell;

import static org.springframework.core.Ordered.HIGHEST_PRECEDENCE;

import org.springframework.core.annotation.Order;
import org.springframework.shell.plugin.BannerProvider;
import org.springframework.stereotype.Component;

/**
 * Provides the welcome banner.
 */
@Component
@Order(HIGHEST_PRECEDENCE)
public class ElasticsearchBannerProvider implements BannerProvider
{
    @Override
    public String getBanner()
    {
        return null;
    }

    @Override
    public String getWelcomeMessage()
    {
        return null;
    }

    @Override
    public String getVersion()
    {
        return "1.2.3";
    }

    @Override
    public String getProviderName()
    {
        return "elasticsearch-shell";
    }
}
