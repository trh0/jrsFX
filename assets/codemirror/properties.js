CodeMirror.defineMode('properties',function(){return{token:function(stream,state){var sol=stream.sol()||state.afterSection;var eol=stream.eol();if(state.afterSection=!1,sol&&(state.nextMultiline?(state.inMultiline=!0,state.nextMultiline=!1):state.position='def'),eol&&!state.nextMultiline&&(state.inMultiline=!1,state.position='def'),sol)while(stream.eatSpace());var ch=stream.next();if(sol&&(ch==='#'||ch==='!'||ch===';'))return state.position='comment',stream.skipToEnd(),'comment';else if(sol&&ch==='[')return state.afterSection=!0,stream.skipTo(']'),stream.eat(']'),'header';else if(ch==='='||ch===':')return state.position='quote',null;else ch==='\\'&&state.position==='quote'&&stream.next()!=='u'&&(state.nextMultiline=!0);return state.position;},startState:function(){return{position:'def',nextMultiline:!1,inMultiline:!1,afterSection:!1};}};}),CodeMirror.defineMIME('text/x-properties','properties'),CodeMirror.defineMIME('text/x-ini','properties');