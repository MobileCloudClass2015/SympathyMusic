#include <stdio.h>
#include <unistd.h>
#include <stdlib.h>
#include <string.h>
#include <sys/socket.h>
#include <sys/stat.h>
#include <arpa/inet.h>
#include <time.h>
#define MAXBUF 128

struct myData
{
	unsigned int num;
	struct timespec ts;
};

struct timespec ts_start;

int main(int argc, char **argv)
{
  int servsock;
  int clen, i;
	unsigned int pnum = 0;
  struct sockaddr_in client_addr, server_addr;
  char buf[MAXBUF];

	char* udpport = NULL;

	if(argc !=2)
	{
		printf("Usage : %s <port>\n", argv[0]);
		exit(1);
	}
	udpport = argv[1];
  //소켓 생성
  if((servsock=socket(AF_INET, SOCK_DGRAM, IPPROTO_UDP))<0) {
    perror("socket error: ");
    exit(1);
  }
  //소켓에 연결하기 위한 서버 옵션 결정
  memset(&server_addr, 0, sizeof(server_addr));
  server_addr.sin_family=AF_INET;
  server_addr.sin_addr.s_addr=htonl(INADDR_ANY);
  server_addr.sin_port=htons(atoi(udpport));
  //소켓에  IP 주소 등의 서버 옵션 설정
  if(bind(servsock, (struct sockaddr *)&server_addr, sizeof(server_addr)) <0) {
    perror("bind error: ");
    exit(1);
  }

	clen=sizeof(client_addr);
  //UDP데이터 수신
  recvfrom(servsock, (void*)buf, MAXBUF, 0, (struct sockaddr *)&client_addr, &clen);

	while(1)
	{
	for(i = 0; i < MAXBUF; ++i)
		buf[i] = ' ';
	clock_gettime(CLOCK_MONOTONIC, &ts_start);
	((struct myData*)buf)->num = pnum++;
	((struct myData*)buf)->ts = ts_start;
	clen=sizeof(client_addr);

  //UDP 데이터 전송
	printf("%d %d%ld\n",((struct myData*)buf)->num, ((struct myData*)buf)->ts.tv_sec,(((struct myData*)buf)->ts.tv_nsec)/1000000);
  sendto(servsock, (void *)buf, MAXBUF, 0, (struct sockaddr *)&client_addr, sizeof(client_addr));
	usleep(20000);
  //종료
	}
  close(servsock);

  return 0;
}
