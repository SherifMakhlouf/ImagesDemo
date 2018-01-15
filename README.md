# Loading images from Flickr

This is a demo application which loads images from Flickr without using any 3rd party libraries at runtime (still using Mockito for tests).

Features:
- Image search
- Infinite scrolling
- Image caching
- Request throttling
- Clean architecture
- Attempt to go reactive without RxJava

## What is in the box?

Project consists of two Gradle modules:

- App itself
- Homemade RxJava-like library module called Pipe.
 
 Wherever you see Pipe - think Observable.

## Architecture

First things first - code is split in packages based on features. Right now we have just search and 
some helper classes.

I went with MVP approach, meaning we have the following layers. From top to bottom.

### Repositories

Repositories are responsible for providing the data. In this app we have just ImagesRepository which
is implemented by FlickrImagesRepository.

This approach means that not only we do not care how exactly data is being loaded but that we are
not even bound to Flickr service itself. One could write implementation for other image services.

Right now this layer is only partially tested. That could be improved, given more time.

### Interactors

Interactors are responsible for business logic.

Here we have ImageSearchInteractor which defines what happens when. This is the most important and
logic-dense class in the whole application (which also took the most time to implement).

I tested all edge cases I could think of. 

If I were to improve something - I would make this class
even less thread-aware by removing synchronization out of it.

### Presenters

Presenters are adapting the data to make it suitable for view to consume.

In current implementation we have just ImageSearchPresenter which converts interactor state into a 
view state.

One interesting thing which is also a responsibility of the presenter - it adds the 
"Loading next page" item to the list of the results. Something which is typically done by the 
adapter was tested, yay. 

### View

View is responsible for displaying the data and providing user interactions back to the presenter.

The most noticeable thing - the view accepts the data only via single method `updateState`. Such
approach *greatly* simplifies debugging and somewhat simplifies the development.

Next thing you could notice is that view state consists of multiple subclasses. That is a Java
version of algebraic data types (ADT for short). It looks a bit weird at first, but in fact it is
also allows us to clearly define the possible states of the view. It would've looked even better in 
Kotlin.

More info on that in my article: https://medium.com/car2godevs/kotlin-adt-74472319962a