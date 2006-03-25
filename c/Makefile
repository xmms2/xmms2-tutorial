LIBS=`pkg-config --libs xmms2-client`

%.o: %.c
	gcc `pkg-config --cflags xmms2-client` -Wall -c -o $@ $< 

tut%: tut%.o
	gcc -o $@ $< $(LIBS)

all: tut1 tut2 tut3 tut4 tut5

clean:
	rm -f tut? *.o

