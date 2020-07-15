# vim-mind-mapper
`vim` based command line mind mapping tool
Create and manipulate complex mind maps using a simple and powerful `Groovy DSL`,
and `vim`, which helps in lesser context switches of brain, and hence lesser disturbance
### Installation
#### Docker (Preferred)
```
docker run -p 8084:8084 -p 8083:8083 -v $(pwd):/home -it anuragntl/vimmindmap:0.1.1
```
It will start `ttyd` server on `port 8084` and tasks can be edited by navigating to:
```
http://localhost:8084/
```
in the browser.

#### Direct
##### Requirements:
* python
* node js (will auto install if `snap` is present)
* groovy (will auto install if `snap` is present)
* vim (will auto install if `snap` is present)

Clone `vim-mind-mapper`:
```
git clone https://github.com/AnuragNtl/vim-mind-mapper.git
```

Install:
```
cd vim-mind-mapper
sudo ./install
```
