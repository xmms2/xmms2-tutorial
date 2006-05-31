LIBS=`pkg-config --libs xmms2-client-cpp`
GLIB=`pkg-config --libs xmms2-client-cpp-glib glib-2.0`

all: tut1 tut2 tut3 tut4 tut5 tut6 tut7 tut8

%.o: %.cc
	g++ `pkg-config --cflags xmms2-client-cpp glib-2.0` -W -Wall -ansi -pedantic -c -o $@ $< 

tut%: tut%.o
	g++ -o $@ $< $(LIBS) $(GLIB)

clean:
	rm -f tut? *.o
