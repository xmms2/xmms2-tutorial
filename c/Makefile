LIBS=`pkg-config --libs xmms2-client`
GLIB=`pkg-config --libs xmms2-client-glib` `pkg-config --libs glib-2.0`

all: tut1 tut2 tut3 tut4 tut5 tut6 tut7 tut8

%.o: %.c
	gcc `pkg-config --cflags xmms2-client glib-2.0` -Wall -c -o $@ $< 

tut%: tut%.o
	gcc -o $@ $< $(LIBS)

tut6: tut6.o
	gcc -o $@ $< $(LIBS) $(GLIB)

tut7: tut7.o
	gcc -o $@ $< $(LIBS) $(GLIB)

tut8: tut8.o
	gcc -o $@ $< $(LIBS) $(GLIB)

clean:
	rm -f tut? *.o

