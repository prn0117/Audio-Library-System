import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.*;
import java.util.List;


public class TestEditActions {

    PlayList p1 = new PlayList("myPlaylist");
    Song s1 = new Song("song1", "artist1");
    Song s2 = new Song("song2", "artist2");
    Song s3 = new Song("song3", "artist3");
    Song s4 = new Song("song4", "artist4");
    Podcast pod1 = new Podcast("podcast1", "host1");
    Podcast.Episode e1 = pod1.createAndAddEpisode("ep1");
    Podcast.Episode e2 = pod1.createAndAddEpisode("ep2");
    Podcast.Episode e3 = pod1.createAndAddEpisode("ep3");
    // fail()

    // TESTING SETNAME
    @Test
    public void testSetNameUndo() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        //PlayList p1 = new PlayList("myPlaylist");
        SetNameCommand s1 = new SetNameCommand(p1, "name1");
        s1.execute();
        Method setNameUndo = SetNameCommand.class.getDeclaredMethod("undo");
        setNameUndo.setAccessible(true);
        setNameUndo.invoke(s1);
        assertEquals("myPlaylist", p1.getName()); // setName undo from name1 back to myPlaylist
    }
    @Test
    public void testSetNameRedo() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        SetNameCommand s1 = new SetNameCommand(p1, "name1");
        SetNameCommand s2 = new SetNameCommand(p1, "name2");
        s1.execute();
        s2.execute();
        Method setNameUndo = SetNameCommand.class.getDeclaredMethod("undo");
        setNameUndo.setAccessible(true);
        setNameUndo.invoke(s2);
        setNameUndo.invoke(s1);
        Method setNameRedo = SetNameCommand.class.getDeclaredMethod("redo");
        setNameRedo.invoke(s1);
        assertEquals("name1", p1.getName());
    }

    //TESTING SHUFFLE
    @Test
    public void testShuffleUndo() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        AddPlayableCommand a1 = new AddPlayableCommand(p1, s1);
        AddPlayableCommand a2 = new AddPlayableCommand(p1, s2);
        AddPlayableCommand a3 = new AddPlayableCommand(p1, s3);
        AddPlayableCommand a4 = new AddPlayableCommand(p1, s4);
        p1.execute(a1);
        p1.execute(a2);
        p1.execute(a3);
        p1.execute(a4);
        List<Playable> list1 = p1.getList(); // Copy of list attribute before shuffling the playlist
        ShuffleCommand sc1 = new ShuffleCommand(p1);
        p1.execute(sc1); // Shuffle the playlist
        Method shuffleUndo = sc1.getClass().getDeclaredMethod("undo");
        shuffleUndo.setAccessible(true);
        shuffleUndo.invoke(sc1);
        assertEquals(p1.getList(), list1);
    }
    @Test
    public void testShuffleRedo() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        AddPlayableCommand a1 = new AddPlayableCommand(p1, s1);
        AddPlayableCommand a2 = new AddPlayableCommand(p1, s2);
        AddPlayableCommand a3 = new AddPlayableCommand(p1, s3);
        AddPlayableCommand a4 = new AddPlayableCommand(p1, s4);
        p1.execute(a1);
        p1.execute(a2);
        p1.execute(a3);
        p1.execute(a4);
        List<Playable> originalList = p1.getList(); // save original list before shuffling
        ShuffleCommand sc1 = new ShuffleCommand(p1);
        p1.execute(sc1); // Shuffle the playlist
        List<Playable> shuffledList = p1.getList(); // save shuffled list by making deep copy
        Method shuffleUndo = sc1.getClass().getDeclaredMethod("undo"); // undo shuffle
        shuffleUndo.setAccessible(true);
        shuffleUndo.invoke(sc1);
        Method shuffleRedo = sc1.getClass().getDeclaredMethod("redo"); // redo shuffle
        shuffleRedo.setAccessible(true);
        // redoing shuffle doesn't produce originally shuffled list
        // it just performs shuffle again
        shuffleRedo.invoke(sc1);
        // test checks that the list now is neither equal to original list nor shuffled list, hence it has been shuffled again
        assertTrue(!p1.getList().equals(shuffledList) && !p1.getList().equals(originalList));
    }

    @Test
    public void testShuffleRedoWithoutUndo() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        AddPlayableCommand a1 = new AddPlayableCommand(p1, s1);
        AddPlayableCommand a2 = new AddPlayableCommand(p1, s2);
        AddPlayableCommand a3 = new AddPlayableCommand(p1, s3);
        AddPlayableCommand a4 = new AddPlayableCommand(p1, s4);
        p1.execute(a1);
        p1.execute(a2);
        p1.execute(a3);
        p1.execute(a4);
        List<Playable> originalList = p1.getList(); // save original list before shuffling
        ShuffleCommand sc1 = new ShuffleCommand(p1);
        p1.execute(sc1); // Shuffle the playlist
        Method shuffleRedo = sc1.getClass().getDeclaredMethod("redo"); // redo shuffle
        shuffleRedo.setAccessible(true);
        shuffleRedo.invoke(sc1);
        assertNotEquals(p1.getList(), originalList);
    }


    // TESTING ADD
    @Test
    public void testAddPlayableUndo() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        AddPlayableCommand a1 = new AddPlayableCommand(p1, s1);
        AddPlayableCommand a2 = new AddPlayableCommand(p1, s2);
        p1.execute(a1);
        p1.execute(a2);
        Method addUndo = AddPlayableCommand.class.getDeclaredMethod("undo");
        addUndo.setAccessible(true);
        addUndo.invoke(a1);
        // Since add(s2) was undone, the last element of list is s1
        Song lastElement = (Song) p1.getList().get(p1.getList().size()-1);
        assertEquals(s1, lastElement);
    }

    @Test
    public void testAddPlayableRedo() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        AddPlayableCommand a1 = new AddPlayableCommand(p1, e1);
        AddPlayableCommand a2 = new AddPlayableCommand(p1, e2);
        AddPlayableCommand a3 = new AddPlayableCommand(p1, e3);
        p1.execute(a1);
        p1.execute(a2);
        p1.execute(a3);
        Method addUndo = AddPlayableCommand.class.getDeclaredMethod("undo");
        addUndo.setAccessible(true);
        Method addRedo = AddPlayableCommand.class.getDeclaredMethod("redo");
        addRedo.setAccessible(true);
        // undo adding episodes 3 and 2, then redo adding episode 2
        addUndo.invoke(a3);
        addUndo.invoke(a2);
        addRedo.invoke(a2);
        Podcast.Episode lastElement = (Podcast.Episode) p1.getList().get(p1.getList().size()-1);
        assertEquals(e2, lastElement); //testing if last element of list is episode 2
    }

    @Test
    public void testAddPlayableRedoWithoutUndo() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        AddPlayableCommand a1 = new AddPlayableCommand(p1, e1);
        AddPlayableCommand a2 = new AddPlayableCommand(p1, e2);
        AddPlayableCommand a3 = new AddPlayableCommand(p1, e3);
        p1.execute(a1);
        p1.execute(a2);
        p1.execute(a3);
        Method addRedo = AddPlayableCommand.class.getDeclaredMethod("redo");
        addRedo.setAccessible(true);
        // redo adding episode 2
        addRedo.invoke(a3);
        addRedo.invoke(a2);
        Podcast.Episode lastElement = (Podcast.Episode) p1.getList().get(p1.getList().size()-1);
        assertEquals(e2, lastElement); //testing if last element of list is episode 2, since we implemented redo without undo
    }

    //TESTING REMOVE
    @Test
    public void testRemovePlayableUndo()throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        AddPlayableCommand a1 = new AddPlayableCommand(p1, s1);
        AddPlayableCommand a2 = new AddPlayableCommand(p1, s2);
        AddPlayableCommand a3 = new AddPlayableCommand(p1, s3);
        p1.execute(a1);
        p1.execute(a2);
        p1.execute(a3);
        RemovePlayableCommand r1 = new RemovePlayableCommand(p1, 0);
        p1.execute(r1); // removes first element of list
        Method removeUndo = RemovePlayableCommand.class.getDeclaredMethod("undo");
        removeUndo.setAccessible(true);
        removeUndo.invoke(r1);
        assertEquals(s1, p1.getList().get(0)); // compare that after undo remove, the first element in list is still song1
    }



    @Test
    public void testRemoveRedo() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        AddPlayableCommand a1 = new AddPlayableCommand(p1, e1);
        AddPlayableCommand a2 = new AddPlayableCommand(p1, e2);
        AddPlayableCommand a3 = new AddPlayableCommand(p1, e3);
        AddPlayableCommand a4 = new AddPlayableCommand(p1, s1);
        AddPlayableCommand a5 = new AddPlayableCommand(p1, s2);
        RemovePlayableCommand r1 = new RemovePlayableCommand(p1, 0);
        p1.execute(a1); // add episodes 1,2,3 and songs 1,2
        p1.execute(a2);
        p1.execute(a3);
        p1.execute(a4);
        p1.execute(a5);
        p1.execute(r1); // remove episode 1

        Method addUndo = AddPlayableCommand.class.getDeclaredMethod("undo");
        Method addRedo = AddPlayableCommand.class.getDeclaredMethod("redo");
        Method removeUndo = RemovePlayableCommand.class.getDeclaredMethod("undo");
        Method removeRedo = RemovePlayableCommand.class.getDeclaredMethod("redo");
        addUndo.setAccessible(true);
        addRedo.setAccessible(true);
        removeUndo.setAccessible(true);
        removeRedo.setAccessible(true);

        removeUndo.invoke(r1); // undo removing episode 1
        // undo adding songs 1,2
        addUndo.invoke(a5);
        addUndo.invoke(a4);
        // redo adding songs 1,2
        addRedo.invoke(a4);
        addRedo.invoke(a5);
        // redo removing first and second element i.e. episode 1 and episode 2
        removeRedo.invoke(r1);
        removeRedo.invoke(r1);
        // final state of list: [ep3, s1, s2], checked by test
        assertTrue(e3.equals(p1.getList().get(0)) && s2.equals(p1.getList().get(p1.getList().size()-1)));
    }

    @Test
    public void testRemoveRedoWithoutUndo() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        AddPlayableCommand a1 = new AddPlayableCommand(p1, e1);
        AddPlayableCommand a2 = new AddPlayableCommand(p1, e2);
        AddPlayableCommand a3 = new AddPlayableCommand(p1, e3);
        AddPlayableCommand a4 = new AddPlayableCommand(p1, s1);
        AddPlayableCommand a5 = new AddPlayableCommand(p1, s2);
        RemovePlayableCommand r1 = new RemovePlayableCommand(p1, 0);
        p1.execute(a1); // add episodes 1,2,3 and songs 1,2
        p1.execute(a2);
        p1.execute(a3);
        p1.execute(a4);
        p1.execute(a5);
        p1.execute(r1); // remove episode 1

        Method addUndo = AddPlayableCommand.class.getDeclaredMethod("undo");
        Method addRedo = AddPlayableCommand.class.getDeclaredMethod("redo");
        Method removeUndo = RemovePlayableCommand.class.getDeclaredMethod("undo");
        Method removeRedo = RemovePlayableCommand.class.getDeclaredMethod("redo");
        addUndo.setAccessible(true);
        addRedo.setAccessible(true);
        removeUndo.setAccessible(true);
        removeRedo.setAccessible(true);

        removeUndo.invoke(r1); // undo removing episode 1
        // undo adding songs 1,2
        addUndo.invoke(a5);
        addUndo.invoke(a4);
        // redo adding songs 1,2
        addRedo.invoke(a4);
        addRedo.invoke(a5);
        // redo removing first, second and third element i.e. episodes 1,2,3
        removeRedo.invoke(r1);
        removeRedo.invoke(r1);
        removeRedo.invoke(r1);
        // final state of list: [s1, s2], checked by test
        assertTrue(s1.equals(p1.getList().get(0)) && s2.equals(p1.getList().get(p1.getList().size()-1)));
    }
}
