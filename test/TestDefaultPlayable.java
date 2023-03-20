import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.lang.reflect.*;


public class TestDefaultPlayable {

    @Test
    public void testSongClone(){
        Song s1 = new Song("song1", "artist1");
        Song s2 = s1.clone();
        assertEquals(s1.getTitle(), s2.getTitle());
        assertEquals(s1.getArtist(), s2.getArtist());
        assertNotSame(s1, s2);
    }

    @Test
    public void testEpisodeClone(){
        Podcast p1 = new Podcast("podcast1", "host1");
        Podcast.Episode e1 = p1.createAndAddEpisode("ep1");
        Podcast.Episode e2 = e1.clone();
        assertEquals(e1.getTitle(), e2.getTitle());
        assertEquals(e1.getEpisodeNumber(), e2.getEpisodeNumber());
        assertNotSame(e1, e2);
    }

    @Test
    public void testPlayListClone(){
        PlayList p1 = new PlayList("playlist1");
        PlayList p2 = p1.clone();
        assertEquals(p1.getName(), p2.getName());
        assertNotSame(p1,p2);
        assertNotSame(p1.getList(), p2.getList());
    }

    @Test
    public void testSetPlayable() throws NoSuchFieldException{
        Library.createLibrary("myLib");
        Library l1 = Library.getLibrary();
        Playable p1 =  new Song("song1", "artist1");
        l1.setPlayable(p1);
        Field aPlayable = Library.class.getDeclaredField("aPlayable");
        aPlayable.setAccessible(true);
        assertEquals(l1.getaPlayable(), p1); // made a specific method to getPlayable field from Library class
    }

    @Test
    public void testCreatePlayable(){
        Library.createLibrary("myLib");
        Library l1 = Library.getLibrary();
        Song s1 = new Song("song1", "artist1");
        l1.setPlayable(s1);
        Playable p = l1.createPlayable();
        assertEquals(p, s1);
        assertNotSame(p, s1);

    }



}

