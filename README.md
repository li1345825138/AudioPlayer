# AudioPlayer
___
![AudioPlayer](/img/mainScene.png "AudioPlayer")

Java Audio Player(only wav format music file)
Audio Player will read all wav format music files on songs directory

(PS: if didn't have songs directory program will auto create one)

## Functions
___
* This Audio Player will search the "songs" directory for all the WAV music files and list all the music in the left-side music list.
* After you've chosen a song from the left select list, click the "Play" button at the bottom to play music, and "Stop" bottom will also activate.
* Loop check box will play current music over and over again if active.
* If you add new song file into "songs" directory, click "Refresh Music List" bottom to refresh music list.
* Press Play button after selected music from music list, then Play button will change into Pause.
* ~~You could use Keypad 0 or alpha 0 to pause or resume music play, and you could also be done by manual press "Pause" bottom (Once music start, "Play" bottom will turn into "Pause" bottom).~~

## Known Issue
___
 * High usage of CPU (fixed)
   * The music monitor function does too much condition.
   * Create redundant new threads, old threads can continue to be reused without the need for new threads.

## TODO:
___
 * Make music could resume at pause position (√)
 * Implement Music play repeat (√)
 * Refresh Music Play List (√)
 * Use Thread Pool to re-implement MusicControlPanel (√)
 * ~~Implement Key(KEYPAD 0 or number 0) to Play or Pause Music when Audio Window is on focus~~
 * More Feature(maybe)...