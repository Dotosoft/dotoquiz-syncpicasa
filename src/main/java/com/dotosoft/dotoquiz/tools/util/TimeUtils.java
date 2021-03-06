/*
	Copyright 2015 Denis Prasetio
	
	Licensed under the Apache License, Version 2.0 (the "License");
	you may not use this file except in compliance with the License.
	You may obtain a copy of the License at
	
	http://www.apache.org/licenses/LICENSE-2.0
	
	Unless required by applicable law or agreed to in writing, software
	distributed under the License is distributed on an "AS IS" BASIS,
	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
	See the License for the specific language governing permissions and
	limitations under the License.
*/

package com.dotosoft.dotoquiz.tools.util;

import java.io.File;
//import java.time.Instant;
//import java.time.LocalDateTime;
//import java.time.ZoneId;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.google.gdata.data.photos.AlbumEntry;
import com.google.gdata.data.photos.GphotoEntry;
import com.google.gdata.data.photos.PhotoEntry;

/**
 * Wrapper for useful time utilities...
 */
public class TimeUtils {

    public static void sortPhotoEntriesNewestFirst( List<PhotoEntry> photos )
    {
        Collections.sort(photos, new Comparator<PhotoEntry>()
        {
            public int compare(PhotoEntry x, PhotoEntry y)
            {
                return y.getUpdated().compareTo(x.getUpdated());
            }
        });
    }
    
    public static void sortGPhotoEntriesNewestFirst( List<GphotoEntry> photoEntry )
    {
    	Collections.sort(photoEntry, new Comparator<GphotoEntry>()
        {
            public int compare(GphotoEntry x, GphotoEntry y)
            {
                return y.getUpdated().compareTo(x.getUpdated());
            }
        });
    }
    
    public static void sortAlbumEntriesNewestFirst( List<AlbumEntry> albums )
    {
        Collections.sort(albums, new Comparator<AlbumEntry>() {
            public int compare(AlbumEntry x, AlbumEntry y) {
                return y.getUpdated().compareTo(x.getUpdated());
            }
        });
    }

    public static void sortFoldersNewestFirst( List<File> files )
    {
        Collections.sort(files, new Comparator<File>()
        {
            public int compare(File x, File y)
            {
                return Long.compare(y.lastModified(), (x.lastModified()));
            }
        });
    }
}
