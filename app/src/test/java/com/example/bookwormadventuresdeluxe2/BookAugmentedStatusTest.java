package com.example.bookwormadventuresdeluxe2;

import com.example.bookwormadventuresdeluxe2.Utilities.Status;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Test cases for the augmented book status based on context
 * These tests show the basic flow of a book exchange
 * starting from when the potential borrower looks at the book
 * to the borrower returning the book to the owner
 */
public class BookAugmentedStatusTest
{
    private String testBorrower;
    private String anotherUser;
    private String owner;
    private Book book;

    /**
     * Assigns the attributes to be used in the tests
     */
    @Before
    public void setup()
    {
        testBorrower = "testBorrower";
        anotherUser = "anotherUser";
        owner = "owner";
        book = makeBook();
    }

    /**
     * Returns a book with test values in its fields
     *
     * @return Book
     */
    private Book makeBook()
    {
        return new Book(owner, "title", "author",
                "desc", "1234567890",
                Status.Available, "");
    }

    /**
     * Viewing a book that's available
     */
    @Test
    public void testGetAugmentStatusAvailable()
    {
        book.setStatus(Status.Available);
        assertEquals(book.getAugmentStatus(testBorrower), Status.Available);
    }

    /**
     * User requested the book
     */
    @Test
    public void testGetAugmentStatusRequested()
    {
        book.addRequester(testBorrower);
        book.setStatus(Status.Available);
        // The test borrowed should see that it is requested
        assertEquals(book.getAugmentStatus(testBorrower), Status.Requested);
        // The owner should also see the book as requested
        assertEquals(book.getAugmentStatus(owner), Status.Requested);
        // A different user should be able to see it as available
        assertEquals(book.getAugmentStatus(anotherUser), Status.Available);
    }

    /**
     * The book request has been accepted by the owner
     */
    @Test
    public void testGetAugmentStatusAccepted()
    {
        book.addRequester(testBorrower);
        book.setStatus(Status.Accepted);
        // The book should be accepted for borrower and owner
        assertEquals(book.getAugmentStatus(testBorrower), Status.Accepted);
        assertEquals(book.getAugmentStatus(owner), Status.Accepted);
    }

    /**
     * The book is being passed from owner to borrower
     */
    @Test
    public void testGetAugmentStatusBPending()
    {
        book.addRequester(testBorrower);
        book.setStatus(Status.bPending);
        // From the owner's perspective, the borrower already has it
        assertEquals(book.getAugmentStatus(owner), Status.Borrowed);
        // From the borrower's perspective, the book request is just accepted
        assertEquals(book.getAugmentStatus(testBorrower), Status.Accepted);
    }

    /**
     * The book exchange has occurred
     * The borrower now has the book
     */
    @Test
    public void testGetAugmentStatusBorrowed()
    {
        book.addRequester(testBorrower);
        book.setStatus(Status.Borrowed);
        // The book should be borrowed for borrower and owner
        assertEquals(book.getAugmentStatus(testBorrower), Status.Borrowed);
        assertEquals(book.getAugmentStatus(owner), Status.Borrowed);
    }

    /**
     * The book is being passed from borrower back to owner
     */
    @Test
    public void testGetAugmentStatusRPending()
    {
        book.addRequester(testBorrower);
        book.setStatus(Status.rPending);
        // From the borrower's perspective, they've returned the book
        assertEquals(book.getAugmentStatus(testBorrower), Status.Available);
        // From the owner's perspective, the borrower still has the book
        assertEquals(book.getAugmentStatus(owner), Status.Borrowed);
        // potential TODO: Only make the book Available for the borrower
        // assertEquals(book.getAugmentStatus(anotherUser), Status.Borrowed);
    }
}