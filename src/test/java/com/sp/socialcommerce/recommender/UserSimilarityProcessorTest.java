package com.sp.socialcommerce.recommender;

import junit.framework.*;

import java.util.ArrayList;
import java.util.List;
import static org.junit.Assert.*;
import static org.hamcrest.collection.IsIterableContainingInOrder.*;

/**
 * @author <a href="mailto:szymon.przedwojski@amg.net.pl">Szymon Przedwojski</a>
 */
public class UserSimilarityProcessorTest extends TestCase {

    public void testFindSimilarUsers() throws Exception {
        assert true;
    }

    public void testSortUsersDescendingWithSimilarity() throws Exception {
        List<SimilarUser> users = new ArrayList<>();
        List<SimilarUser> usersExpected = new ArrayList<>();

        SimilarUser user1 = new SimilarUser();
        user1.similarity = 10.0;

        SimilarUser user2 = new SimilarUser();
        user2.similarity = 100.0;

        SimilarUser user3 = new SimilarUser();
        user3.similarity = 50.0;

        users.add(user1);
        users.add(user2);
        users.add(user3);

        usersExpected.add(user2);
        usersExpected.add(user3);
        usersExpected.add(user1);

        System.out.println("Users before sort:");
        for(SimilarUser u : users)
            System.out.println(u.similarity);

        UserSimilarityProcessor usp = new UserSimilarityProcessor();
        users = usp.sortUsersDescendingWithSimilarity(users);

        System.out.println("Users:");
        for(SimilarUser u : users)
            System.out.println(u.similarity);

        System.out.println("Users expected:");
        for(SimilarUser u : usersExpected)
            System.out.println(u.similarity);

        assertThat("List equality", users, contains(usersExpected.toArray()));


    }
}