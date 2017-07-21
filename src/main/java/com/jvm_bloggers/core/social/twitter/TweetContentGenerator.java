package com.jvm_bloggers.core.social.twitter;

import com.jvm_bloggers.core.blogpost_redirect.LinkGenerator;
import com.jvm_bloggers.entities.newsletter_issue.NewsletterIssue;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Random;

import static java.lang.String.format;
import static java.util.stream.Collectors.toList;
import static lombok.AccessLevel.PACKAGE;

@Component
@Slf4j
@RequiredArgsConstructor(access = PACKAGE)
class TweetContentGenerator {

    private static final int TWEET_MAX_LENGTH = 140;
    private static final String MESSAGE_TEMPLATE =
        "Nowy numer #%s już online - %s z postami m.in. %s, %s i %s #java #jvm";
    private static final String SHORT_MESSAGE_TEMPLATE =
        "Nowy numer #%s już online - %s z postami m.in. %s i %s #java #jvm";

    private final LinkGenerator linkGenerator;
    private final Random random = new Random();

    public String generateTweetContent(NewsletterIssue issue) {
        final List<String> personalTTs =
            issue.getBlogPosts().stream().map(b -> b.getBlog()).filter(b -> b.isPersonal())
                .map(b -> b.getTwitter()).collect(toList());
        final List<String> companyTTs =
            issue.getBlogPosts().stream().map(b -> b.getBlog()).filter(b -> !b.isPersonal())
                .map(b -> b.getTwitter()).collect(toList());

        final String personalHandle1 = personalTTs.remove(random.nextInt(personalTTs.size()));
        final String personalHandle2 = personalTTs.remove(random.nextInt(personalTTs.size()));
        final String companyHandle = companyTTs.remove(random.nextInt(companyTTs.size()));

        final String issueLink = linkGenerator.generateIssueLink(issue.getIssueNumber());
        final String tweetContent =
            format(
                MESSAGE_TEMPLATE, issue.getIssueNumber(), issueLink,
                personalHandle1, companyHandle, personalHandle2
            );

        if (tweetIsTooLong(tweetContent, issueLink.length())) {
            return format(
                SHORT_MESSAGE_TEMPLATE, issue.getIssueNumber(), issueLink,
                personalHandle1, companyHandle
            );
        } else {
            return tweetContent;
        }
        // check for length - link 23 chars, number 3 chars
    }

    private boolean tweetIsTooLong(String tweetContent, int originalIssuesLinkLength) {
        return (tweetContent.length() - originalIssuesLinkLength + 23) > TWEET_MAX_LENGTH;
    }

}
