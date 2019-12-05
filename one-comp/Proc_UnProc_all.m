% Make a function out of this, that takes the officeIoT folder address and
% the number of runs as variables;

clear

OPB1 = dlmread('officeIoT/RPBWR1', ' ', 0, 1);
HPB1 = dlmread('homeIoT/RPBWR1', ' ', 0, 1);
BPB1 = dlmread('buses/RPBWR1', ' ', 0, 1);
PPB1 = dlmread('non-proc_proc/RPBWR1', ' ', 0, 1);


OPB2 = dlmread('officeIoT/RPBWR2', ' ', 0, 1);
HPB2 = dlmread('homeIoT/RPBWR2', ' ', 0, 1);
BPB2 = dlmread('buses/RPBWR2', ' ', 0, 1);
PPB2 = dlmread('non-proc_proc/RPBWR2', ' ', 0, 1);


OPB3 = dlmread('officeIoT/RPBWR3', ' ', 0, 1);
HPB3 = dlmread('homeIoT/RPBWR3', ' ', 0, 1);
BPB3 = dlmread('buses/RPBWR3', ' ', 0, 1);
PPB3 = dlmread('non-proc_proc/RPBWR3', ' ', 0, 1);


OPB4 = dlmread('officeIoT/RPBWR4', ' ', 0, 1);
HPB4 = dlmread('homeIoT/RPBWR4', ' ', 0, 1);
BPB4 = dlmread('buses/RPBWR4', ' ', 0, 1);
PPB4 = dlmread('non-proc_proc/RPBWR4', ' ', 0, 1);


OPB5 = dlmread('officeIoT/RPBWR5', ' ', 0, 1);
HPB5 = dlmread('homeIoT/RPBWR5', ' ', 0, 1);
BPB5 = dlmread('buses/RPBWR5', ' ', 0, 1);
PPB5 = dlmread('non-proc_proc/RPBWR5', ' ', 0, 1);


OPB6 = dlmread('officeIoT/RPBWR6', ' ', 0, 1);
HPB6 = dlmread('homeIoT/RPBWR6', ' ', 0, 1);
BPB6 = dlmread('buses/RPBWR6', ' ', 0, 1);
PPB6 = dlmread('non-proc_proc/RPBWR6', ' ', 0, 1);

OUB1 = dlmread('officeIoT/RUPBWR1', ' ', 0, 1);
HUB1 = dlmread('homeIoT/RUPBWR1', ' ', 0, 1);
BUB1 = dlmread('buses/RUPBWR1', ' ', 0, 1);
PUB1 = dlmread('non-proc_proc/RUPBWR1', ' ', 0, 1);


OUB2 = dlmread('officeIoT/RUPBWR2', ' ', 0, 1);
HUB2 = dlmread('homeIoT/RUPBWR2', ' ', 0, 1);
BUB2 = dlmread('buses/RUPBWR2', ' ', 0, 1);
PUB2 = dlmread('non-proc_proc/RUPBWR2', ' ', 0, 1);


OUB3 = dlmread('officeIoT/RUPBWR3', ' ', 0, 1);
HUB3 = dlmread('homeIoT/RUPBWR3', ' ', 0, 1);
BUB3 = dlmread('buses/RUPBWR3', ' ', 0, 1);
PUB3 = dlmread('non-proc_proc/RUPBWR3', ' ', 0, 1);


OUB4 = dlmread('officeIoT/RUPBWR4', ' ', 0, 1);
HUB4 = dlmread('homeIoT/RUPBWR4', ' ', 0, 1);
BUB4 = dlmread('buses/RUPBWR4', ' ', 0, 1);
PUB4 = dlmread('non-proc_proc/RUPBWR4', ' ', 0, 1);


OUB5 = dlmread('officeIoT/RUPBWR5', ' ', 0, 1);
HUB5 = dlmread('homeIoT/RUPBWR5', ' ', 0, 1);
BUB5 = dlmread('buses/RUPBWR5', ' ', 0, 1);
PUB5 = dlmread('non-proc_proc/RUPBWR5', ' ', 0, 1);


OUB6 = dlmread('officeIoT/RUPBWR6', ' ', 0, 1);
HUB6 = dlmread('homeIoT/RUPBWR6', ' ', 0, 1);
BUB6 = dlmread('buses/RUPBWR6', ' ', 0, 1);
PUB6 = dlmread('non-proc_proc/RUPBWR6', ' ', 0, 1);


[r3, c3] = size(OPB1);

for repo = 1:c3
    oproc_up(repo, 1) = mean(OPB1(:, repo))/(mean(OUB1(:, repo))+mean(OPB1(:, repo)));
    if (isnan(mean(OPB1(:, repo))/(mean(OUB1(:, repo))+mean(OPB1(:, repo)))))
        oproc_up(repo, 1) = 0;
    end
    oproc_up(repo, 2) = mean(OPB2(:, repo))/(mean(OUB2(:, repo))+mean(OPB2(:, repo)));
    if (isnan(mean(OPB2(:, repo))/(mean(OUB2(:, repo))+mean(OPB2(:, repo)))))
        oproc_up(repo, 2) = 0;
    end
    oproc_up(repo, 3) = mean(OPB3(:, repo))/(mean(OUB3(:, repo))+mean(OPB3(:, repo)));
    if (isnan(mean(OPB3(:, repo))/(mean(OUB3(:, repo))+mean(OPB3(:, repo)))))
        oproc_up(repo, 3) = 0;
    end
    oproc_up(repo, 4) = mean(OPB4(:, repo))/(mean(OUB4(:, repo))+mean(OPB4(:, repo)));
    if (isnan(mean(OPB4(:, repo))/(mean(OUB4(:, repo))+mean(OPB4(:, repo)))))
        oproc_up(repo, 4) = 0;
    end
    oproc_up(repo, 5) = mean(OPB5(:, repo))/(mean(OUB5(:, repo))+mean(OPB5(:, repo)));
    if (isnan(mean(OPB5(:, repo))/(mean(OUB5(:, repo))+mean(OPB5(:, repo)))))
        oproc_up(repo, 5) = 0;
    end
    oproc_up(repo, 6) =  mean(OPB6(:, repo))/(mean(OUB6(:, repo))+mean(OPB6(:, repo)));
    if (isnan(mean(OPB6(:, repo))/(mean(OUB6(:, repo))+mean(OPB6(:, repo)))))
        oproc_up(repo, 6) = 0;
    end
    hproc_up(repo, 1) = mean(HPB1(:, repo))/(mean(HUB1(:, repo))+mean(HPB1(:, repo)));
    if (isnan(mean(HPB1(:, repo))/(mean(HUB1(:, repo))+mean(HPB1(:, repo)))))
        hproc_up(repo, 1) = 0;
    end
    hproc_up(repo, 2) = mean(HPB2(:, repo))/(mean(HUB2(:, repo))+mean(HPB2(:, repo)));
    if (isnan(mean(HPB2(:, repo))/(mean(HUB2(:, repo))+mean(HPB2(:, repo)))))
        hproc_up(repo, 2) = 0;
    end
    hproc_up(repo, 3) = mean(HPB3(:, repo))/(mean(HUB3(:, repo))+mean(HPB3(:, repo)));
    if (isnan(mean(HPB3(:, repo))/(mean(HUB3(:, repo))+mean(HPB3(:, repo)))))
        hproc_up(repo, 3) = 0;
    end
    hproc_up(repo, 4) = mean(HPB4(:, repo))/(mean(HUB4(:, repo))+mean(HPB4(:, repo)));
    if (isnan(mean(HPB4(:, repo))/(mean(HUB4(:, repo))+mean(HPB4(:, repo)))))
        hproc_up(repo, 4) = 0;
    end
    hproc_up(repo, 5) = mean(HPB6(:, repo))/(mean(HUB5(:, repo))+mean(HPB5(:, repo)));
    if (isnan(mean(HPB6(:, repo))/(mean(HUB5(:, repo))+mean(HPB5(:, repo)))))
        hproc_up(repo, 5) = 0;
    end
    hproc_up(repo, 6) = mean(HPB6(:, repo))/(mean(HUB6(:, repo))+mean(HPB6(:, repo)));
    if (isnan(mean(HPB6(:, repo))/(mean(HUB6(:, repo))+mean(HPB6(:, repo)))))
        hproc_up(repo, 6) = 0;
    end
    bproc_up(repo, 1) = mean(BPB1(:, repo))/(mean(BUB1(:, repo))+mean(BPB1(:, repo)));
    if (isnan(mean(BPB1(:, repo))/(mean(BUB1(:, repo))+mean(BPB1(:, repo)))))
        bproc_up(repo, 1) = 0;
    end
    bproc_up(repo, 2) = mean(BPB2(:, repo))/(mean(BUB2(:, repo))+mean(BPB2(:, repo)));
    if (isnan(mean(BPB2(:, repo))/(mean(BUB2(:, repo))+mean(BPB2(:, repo)))))
        bproc_up(repo, 2) = 0;
    end
    bproc_up(repo, 3) = mean(BPB3(:, repo))/(mean(BUB3(:, repo))+mean(BPB3(:, repo)));
    if (isnan(mean(BPB3(:, repo))/(mean(BUB3(:, repo))+mean(BPB3(:, repo)))))
        bproc_up(repo, 3) = 0;
    end
    bproc_up(repo, 4) = mean(BPB4(:, repo))/(mean(BUB4(:, repo))+mean(BPB4(:, repo)));
    if (isnan(mean(BPB4(:, repo))/(mean(BUB4(:, repo))+mean(BPB4(:, repo)))))
        bproc_up(repo, 4) = 0;
    end
    bproc_up(repo, 5) = mean(BPB5(:, repo))/(mean(BUB5(:, repo))+mean(BPB5(:, repo)));
    if (isnan(mean(BPB5(:, repo))/(mean(BUB5(:, repo))+mean(BPB5(:, repo)))))
        bproc_up(repo, 5) = 0;
    end
    bproc_up(repo, 6) =  mean(BPB6(:, repo))/(mean(BUB6(:, repo))+mean(BPB6(:, repo)));
    if (isnan(mean(BPB6(:, repo))/(mean(BUB6(:, repo))+mean(BPB6(:, repo)))))
        bproc_up(repo, 6) = 0;
    end
    pproc_up(repo, 1) = mean(PPB1(:, repo))/(mean(PUB1(:, repo))+mean(PPB1(:, repo)));
    if (isnan(mean(PPB1(:, repo))/(mean(PUB1(:, repo))+mean(PPB1(:, repo)))))
        pproc_up(repo, 1) = 0;
    end
    pproc_up(repo, 2) = mean(PPB2(:, repo))/(mean(PUB2(:, repo))+mean(PPB2(:, repo)));
    if (isnan(mean(PPB2(:, repo))/(mean(PUB2(:, repo))+mean(PPB2(:, repo)))))
        pproc_up(repo, 2) = 0;
    end
    pproc_up(repo, 3) = mean(PPB3(:, repo))/(mean(PUB3(:, repo))+mean(PPB3(:, repo)));
    if (isnan(mean(PPB3(:, repo))/(mean(PUB3(:, repo))+mean(PPB3(:, repo)))))
        pproc_up(repo, 3) = 0;
    end
    pproc_up(repo, 4) = mean(PPB4(:, repo))/(mean(PUB4(:, repo))+mean(PPB4(:, repo)));
    if (isnan(mean(PPB4(:, repo))/(mean(PUB4(:, repo))+mean(PPB4(:, repo)))))
        pproc_up(repo, 4) = 0;
    end
    pproc_up(repo, 5) = mean(PPB5(:, repo))/(mean(PUB5(:, repo))+mean(PPB5(:, repo)));
    if (isnan(mean(PPB5(:, repo))/(mean(PUB5(:, repo))+mean(PPB5(:, repo)))))
        pproc_up(repo, 5) = 0;
    end
    pproc_up(repo, 6) =  mean(PPB6(:, repo))/(mean(PUB6(:, repo))+mean(PPB6(:, repo)));
    if (isnan(mean(PPB6(:, repo))/(mean(PUB6(:, repo))+mean(PPB6(:, repo)))))
        pproc_up(repo, 6) = 0;
    end
    
    
    oproc(repo, 1) = mean(OPB1(:, repo));
    if (isnan(mean(OPB1(:, repo))))
        oproc(repo, 1) = 0;
    end
    oproc(repo, 2) = mean(OPB2(:, repo));
    if (isnan(mean(OPB2(:, repo))))
        oproc(repo, 2) = 0;
    end
    oproc(repo, 3) = mean(OPB3(:, repo));
    if (isnan(mean(OPB3(:, repo))))
        oproc(repo, 3) = 0;
    end
    oproc(repo, 4) = mean(OPB4(:, repo));
    if (isnan(mean(OPB4(:, repo))))
        oproc(repo, 4) = 0;
    end
    oproc(repo, 5) = mean(OPB5(:, repo));
    if (isnan(mean(OPB5(:, repo))))
        oproc(repo, 5) = 0;
    end
    oproc(repo, 6) =  mean(OPB6(:, repo));
    if (isnan(mean(OPB6(:, repo))))
        oproc(repo, 6) = 0;
    end
    hproc(repo, 1) = mean(HPB1(:, repo));
    if (isnan(mean(HPB1(:, repo))))
        hproc(repo, 1) = 0;
    end
    hproc(repo, 2) = mean(HPB2(:, repo));
    if (isnan(mean(HPB2(:, repo))))
        hproc(repo, 2) = 0;
    end
    hproc(repo, 3) = mean(HPB3(:, repo));
    if (isnan(mean(HPB3(:, repo))))
        hproc(repo, 3) = 0;
    end
    hproc(repo, 4) = mean(HPB4(:, repo));
    if (isnan(mean(HPB4(:, repo))))
        hproc(repo, 4) = 0;
    end
    hproc(repo, 5) = mean(HPB6(:, repo));
    if (isnan(mean(HPB6(:, repo))))
        hproc(repo, 5) = 0;
    end
    hproc(repo, 6) = mean(HPB6(:, repo));
    if (isnan(mean(HPB6(:, repo))))
        hproc(repo, 6) = 0;
    end
    bproc(repo, 1) = mean(BPB1(:, repo));
    if (isnan(mean(BPB1(:, repo))))
        bproc(repo, 1) = 0;
    end
    bproc(repo, 2) = mean(BPB2(:, repo));
    if (isnan(mean(BPB2(:, repo))))
        bproc(repo, 2) = 0;
    end
    bproc(repo, 3) = mean(BPB3(:, repo));
    if (isnan(mean(BPB3(:, repo))))
        bproc(repo, 3) = 0;
    end
    bproc(repo, 4) = mean(BPB4(:, repo));
    if (isnan(mean(BPB4(:, repo))))
        bproc(repo, 4) = 0;
    end
    bproc(repo, 5) = mean(BPB5(:, repo));
    if (isnan(mean(BPB5(:, repo))))
        bproc(repo, 5) = 0;
    end
    bproc(repo, 6) =  mean(BPB6(:, repo));
    if (isnan(mean(BPB6(:, repo))))
        bproc(repo, 6) = 0;
    end
    pproc(repo, 1) = mean(PPB1(:, repo));
    if (isnan(mean(PPB1(:, repo))))
        pproc(repo, 1) = 0;
    end
    pproc(repo, 2) = mean(PPB2(:, repo));
    if (isnan(mean(PPB2(:, repo))))
        pproc(repo, 2) = 0;
    end
    pproc(repo, 3) = mean(PPB3(:, repo));
    if (isnan(mean(PPB3(:, repo))))
        pproc(repo, 3) = 0;
    end
    pproc(repo, 4) = mean(PPB4(:, repo));
    if (isnan(mean(PPB4(:, repo))))
        pproc(repo, 4) = 0;
    end
    pproc(repo, 5) = mean(PPB5(:, repo));
    if (isnan(mean(PPB5(:, repo))))
        pproc(repo, 5) = 0;
    end
    pproc(repo, 6) =  mean(PPB6(:, repo));
    if (isnan(mean(PPB6(:, repo))))
        pproc(repo, 6) = 0;
    end
    ouproc(repo, 1) = mean(OUB1(:, repo));
    if (isnan(mean(OUB1(:, repo))))
        ouproc(repo, 1) = 0;
    end
    ouproc(repo, 2) = mean(OUB2(:, repo));
    if (isnan(mean(OUB2(:, repo))))
        ouproc(repo, 2) = 0;
    end
    ouproc(repo, 3) = mean(OUB3(:, repo));
    if (isnan(mean(OUB3(:, repo))))
        ouproc(repo, 3) = 0;
    end
    ouproc(repo, 4) = mean(OUB4(:, repo));
    if (isnan(mean(OUB4(:, repo))))
        ouproc(repo, 4) = 0;
    end
    ouproc(repo, 5) = mean(OUB5(:, repo));
    if (isnan(mean(OUB5(:, repo))))
        ouproc(repo, 5) = 0;
    end
    ouproc(repo, 6) =  mean(OUB6(:, repo));
    if (isnan(mean(OUB6(:, repo))))
        ouproc(repo, 6) = 0;
    end
    huproc(repo, 1) = mean(HUB1(:, repo));
    if (isnan(mean(HUB1(:, repo))))
        huproc(repo, 1) = 0;
    end
    huproc(repo, 2) = mean(HUB2(:, repo));
    if (isnan(mean(HUB2(:, repo))))
        huproc(repo, 2) = 0;
    end
    huproc(repo, 3) = mean(HUB3(:, repo));
    if (isnan(mean(HUB3(:, repo))))
        huproc(repo, 3) = 0;
    end
    huproc(repo, 4) = mean(HUB4(:, repo));
    if (isnan(mean(HUB4(:, repo))))
        huproc(repo, 4) = 0;
    end
    huproc(repo, 5) = mean(HUB6(:, repo));
    if (isnan(mean(HUB6(:, repo))))
        huproc(repo, 5) = 0;
    end
    huproc(repo, 6) = mean(HUB6(:, repo));
    if (isnan(mean(HUB6(:, repo))))
        huproc(repo, 6) = 0;
    end
    buproc(repo, 1) = mean(BUB1(:, repo));
    if (isnan(mean(BUB1(:, repo))))
        buproc(repo, 1) = 0;
    end
    buproc(repo, 2) = mean(BUB2(:, repo));
    if (isnan(mean(BUB2(:, repo))))
        buproc(repo, 2) = 0;
    end
    buproc(repo, 3) = mean(BUB3(:, repo));
    if (isnan(mean(BUB3(:, repo))))
        buproc(repo, 3) = 0;
    end
    buproc(repo, 4) = mean(BUB4(:, repo));
    if (isnan(mean(BUB4(:, repo))))
        buproc(repo, 4) = 0;
    end
    buproc(repo, 5) = mean(BUB5(:, repo));
    if (isnan(mean(BUB5(:, repo))))
        buproc(repo, 5) = 0;
    end
    buproc(repo, 6) =  mean(BUB6(:, repo));
    if (isnan(mean(BUB6(:, repo))))
        buproc(repo, 6) = 0;
    end
    puproc(repo, 1) = mean(PUB1(:, repo));
    if (isnan(mean(PUB1(:, repo))))
        puproc(repo, 1) = 0;
    end
    puproc(repo, 2) = mean(PUB2(:, repo));
    if (isnan(mean(PUB2(:, repo))))
        puproc(repo, 2) = 0;
    end
    puproc(repo, 3) = mean(PUB3(:, repo));
    if (isnan(mean(PUB3(:, repo))))
        puproc(repo, 3) = 0;
    end
    puproc(repo, 4) = mean(PUB4(:, repo));
    if (isnan(mean(PUB4(:, repo))))
        puproc(repo, 4) = 0;
    end
    puproc(repo, 5) = mean(PUB5(:, repo));
    if (isnan(mean(PUB5(:, repo))))
        puproc(repo, 5) = 0;
    end
    puproc(repo, 6) =  mean(PUB6(:, repo));
    if (isnan(mean(PUB6(:, repo))))
        puproc(repo, 6) = 0;
    end
end


figure

% subplot(2,1,1);
% yyaxis left
bar_handle = bar([oproc_up(:,1), oproc_up(:,2), oproc_up(:,3), oproc_up(:,4), oproc_up(:,5), oproc_up(:,6)]);
% title('Processing threads','fontsize',16)
xlabel('Repository number','fontsize',12) 
ylabel('Processed/Total Processing Messages (100%)','fontsize',12)
% xlim([17 48]);
set(bar_handle(1),'FaceColor',[0,0.5,1])
set(bar_handle(2),'FaceColor',[0,1,0])




figure

% subplot(2,1,1);
% yyaxis left
bar([mean(nonzeros(oproc_up(:,1))), mean(nonzeros(oproc_up(:,2))), mean(nonzeros(oproc_up(:,3))), mean(nonzeros(oproc_up(:,4))), mean(nonzeros(oproc_up(:,5))), mean(nonzeros(oproc_up(:,6)));
    mean(nonzeros(hproc_up(:,1))), mean(nonzeros(hproc_up(:,2))), mean(nonzeros(hproc_up(:,3))), mean(nonzeros(hproc_up(:,4))), mean(nonzeros(hproc_up(:,5))), mean(nonzeros(hproc_up(:,6)));
    mean(nonzeros(bproc_up(:,1))), mean(nonzeros(bproc_up(:,2))), mean(nonzeros(bproc_up(:,3))), mean(nonzeros(bproc_up(:,4))), mean(nonzeros(bproc_up(:,5))), mean(nonzeros(bproc_up(:,6)));
    mean(nonzeros(pproc_up(:,1))), mean(nonzeros(pproc_up(:,2))), mean(nonzeros(pproc_up(:,3))), mean(nonzeros(pproc_up(:,4))), mean(nonzeros(pproc_up(:,5))), mean(nonzeros(pproc_up(:,6)))]);
% title('Processing threads','fontsize',16)
xlabel('Scenario Number','fontsize',12) 
ylabel('Processed/Total Processing Messages (100%)','fontsize',12)
%TSDS:
% Modify this:
% lgd1 =legend('1: f; 1.1; 100; 3:1 \newline2: f; 3; 100; 4:1; \newline3: t; 4; 100; 4:1; \newline4: t; 2; 100; 1:2', ...
%             '1: f; 1.3; 100; 3:1 \newline2: f; 3.2; 100; 4:1; \newline3: t; 2.2; 500; 4:1;  \newline4: t; 2; 500; 2:2', ...
%             '1: f; 2; 100; 3:1 \newline2: f; 50; 100; 4:1; \newline3: f; 3; 100 4:1; \newline4: f; 2; 100; 4:2', ...
%             '1: f; 1.6; 300; 3:1 \newline2: f; 3; 200; 4:1; \newline3: f; 4; 300 4:1; \newline4: f; 2; 300; 5:2', ...
%             '1: f; 2.2; 600; 3:1 \newline2: f; 3.2; 300; 4:1; \newline3: f; 2.2; 600 4:1; \newline4: f; 2; 600; 3:1', ...
%             '1: f; 2; 900; 4:1 \newline2: f; 50; 600; 4:1; \newline3: f; 3; 900 4:1; \newline4: f; 2; 900; 4:1', 'Location', 'southoutside');
% title(lgd1, 'Scenario Number: Storage Mode; Processing Delay; proc Freshness Period; proc Shelf-Life; non-proc Shelf-Life; Compress:Delete Ratio; Non-Proc:Proc Ratio');
% lgd1.NumColumns = 6;
% xlim([17 48]);
% set(bar_handle(1),'FaceColor',[0,0.5,1])
% set(bar_handle(2),'FaceColor',[0,1,0])

figure

% subplot(2,1,1);
% yyaxis left
bar([mean(oproc(:,1)), mean(ouproc(:,1)), mean(oproc(:,2)), mean(ouproc(:,2)), mean(oproc(:,3)), mean(ouproc(:,3)), mean(oproc(:,4)), mean(ouproc(:,4)), mean(oproc(:,5)), mean(ouproc(:,5)), mean(oproc(:,6)), mean(ouproc(:,6));
    mean(hproc(:,1)), mean(huproc(:,1)), mean(hproc(:,2)), mean(huproc(:,2)), mean(hproc(:,3)), mean(huproc(:,3)), mean(hproc(:,4)), mean(huproc(:,4)), mean(hproc(:,5)), mean(huproc(:,5)), mean(hproc(:,6)), mean(huproc(:,6));
    mean(bproc(:,1)), mean(buproc(:,1)), mean(bproc(:,2)), mean(buproc(:,2)), mean(bproc(:,3)), mean(buproc(:,3)), mean(bproc(:,4)), mean(buproc(:,4)), mean(bproc(:,5)), mean(buproc(:,5)), mean(bproc(:,6)), mean(buproc(:,6));
    mean(pproc(:,1)), mean(puproc(:,1)), mean(pproc(:,2)), mean(puproc(:,2)), mean(pproc(:,3)), mean(puproc(:,3)), mean(pproc(:,4)), mean(puproc(:,4)), mean(pproc(:,5)), mean(puproc(:,5)), mean(pproc(:,6)), mean(puproc(:,6))]);
% title('Processing threads','fontsize',16)
xlabel('Scenario Number','fontsize',12) 
ylabel('Processed/UnProcessed Messages BW (B/s)','fontsize',12)
ylim([0 15e5]);


OPF1 = dlmread('officeIoT/RPFR1', ' ', 0, 1);
HPF1 = dlmread('homeIoT/RPFR1', ' ', 0, 1);
BPF1 = dlmread('buses/RPFR1', ' ', 0, 1);
PPF1 = dlmread('non-proc_proc/RPFR1', ' ', 0, 1);


OPF2 = dlmread('officeIoT/RPFR2', ' ', 0, 1);
HPF2 = dlmread('homeIoT/RPFR2', ' ', 0, 1);
BPF2 = dlmread('buses/RPFR2', ' ', 0, 1);
PPF2 = dlmread('non-proc_proc/RPFR2', ' ', 0, 1);


OPF3 = dlmread('officeIoT/RPFR3', ' ', 0, 1);
HPF3 = dlmread('homeIoT/RPFR3', ' ', 0, 1);
BPF3 = dlmread('buses/RPFR3', ' ', 0, 1);
PPF3 = dlmread('non-proc_proc/RPFR3', ' ', 0, 1);


OPF4 = dlmread('officeIoT/RPFR4', ' ', 0, 1);
HPF4 = dlmread('homeIoT/RPFR4', ' ', 0, 1);
BPF4 = dlmread('buses/RPFR4', ' ', 0, 1);
PPF4 = dlmread('non-proc_proc/RPFR4', ' ', 0, 1);


OPF5 = dlmread('officeIoT/RPFR5', ' ', 0, 1);
HPF5 = dlmread('homeIoT/RPFR5', ' ', 0, 1);
BPF5 = dlmread('buses/RPFR5', ' ', 0, 1);
PPF5 = dlmread('non-proc_proc/RPFR5', ' ', 0, 1);


OPF6 = dlmread('officeIoT/RPFR6', ' ', 0, 1);
HPF6 = dlmread('homeIoT/RPFR6', ' ', 0, 1);
BPF6 = dlmread('buses/RPFR6', ' ', 0, 1);
PPF6 = dlmread('non-proc_proc/RPFR6', ' ', 0, 1);


[r3, c3] = size(OPF1);

for repo = 1:c3
    ofresh_perc(repo, 1) = OPF1(1, repo)/sum(OPF1(:, repo));
    if (isnan(OPF1(1, repo)/sum(OPF1(:, repo))))
        ofresh_perc(repo, 1) = 0;
    end
    ofresh_perc(repo, 2) = OPF2(1, repo)/sum(OPF2(:, repo));
    if (isnan(OPF2(1, repo)/sum(OPF2(:, repo))))
        ofresh_perc(repo, 2) = 0;
    end
    ofresh_perc(repo, 3) = OPF3(1, repo)/sum(OPF3(:, repo));
    if (isnan(OPF3(1, repo)/sum(OPF3(:, repo))))
        ofresh_perc(repo, 3) = 0;
    end
    ofresh_perc(repo, 4) = OPF4(1, repo)/sum(OPF4(:, repo));
    if (isnan(OPF4(1, repo)/sum(OPF4(:, repo))))
        ofresh_perc(repo, 4) = 0;
    end
    ofresh_perc(repo, 5) = OPF5(1, repo)/sum(OPF5(:, repo));
    if (isnan(OPF5(1, repo)/sum(OPF5(:, repo))))
        ofresh_perc(repo, 5) = 0;
    end
    ofresh_perc(repo, 6) =  OPF6(1, repo)/sum(OPF6(:, repo));
    if (isnan(OPF6(1, repo)/sum(OPF6(:, repo))))
        ofresh_perc(repo, 6) = 0;
    end
    hfresh_perc(repo, 1) = HPF1(1, repo)/sum(HPF1(:, repo));
    if (isnan(HPF1(1, repo)/sum(HPF1(:, repo))))
        hfresh_perc(repo, 1) = 0;
    end
    hfresh_perc(repo, 2) = HPF2(1, repo)/sum(HPF2(:, repo));
    if (isnan(HPF2(1, repo)/sum(HPF2(:, repo))))
        hfresh_perc(repo, 2) = 0;
    end
    hfresh_perc(repo, 3) = HPF3(1, repo)/sum(HPF3(:, repo));
    if (isnan(HPF3(1, repo)/sum(HPF3(:, repo))))
        hfresh_perc(repo, 3) = 0;
    end
    hfresh_perc(repo, 4) = HPF4(1, repo)/sum(HPF4(:, repo));
    if (isnan(HPF4(1, repo)/sum(HPF4(:, repo))))
        hfresh_perc(repo, 4) = 0;
    end
    hfresh_perc(repo, 5) = HPF5(1, repo)/sum(HPF5(:, repo));
    if (isnan(HPF5(1, repo)/sum(HPF5(:, repo))))
        hfresh_perc(repo, 5) = 0;
    end
    hfresh_perc(repo, 6) =  HPF6(1, repo)/sum(HPF6(:, repo));
    if (isnan(HPF6(1, repo)/sum(HPF6(:, repo))))
        hfresh_perc(repo, 6) = 0;
    end
    bfresh_perc(repo, 1) = BPF1(1, repo)/sum(BPF1(:, repo));
    if (isnan(BPF1(1, repo)/sum(BPF1(:, repo))))
        bfresh_perc(repo, 1) = 0;
    end
    bfresh_perc(repo, 2) = BPF2(1, repo)/sum(BPF2(:, repo));
    if (isnan(BPF2(1, repo)/sum(BPF2(:, repo))))
        bfresh_perc(repo, 2) = 0;
    end
    bfresh_perc(repo, 3) = BPF3(1, repo)/sum(BPF3(:, repo));
    if (isnan(BPF3(1, repo)/sum(BPF3(:, repo))))
        bfresh_perc(repo, 3) = 0;
    end
    bfresh_perc(repo, 4) = BPF4(1, repo)/sum(BPF4(:, repo));
    if (isnan(BPF4(1, repo)/sum(BPF4(:, repo))))
        bfresh_perc(repo, 4) = 0;
    end
    bfresh_perc(repo, 5) = BPF5(1, repo)/sum(BPF5(:, repo));
    if (isnan(BPF5(1, repo)/sum(BPF5(:, repo))))
        bfresh_perc(repo, 5) = 0;
    end
    bfresh_perc(repo, 6) =  BPF6(1, repo)/sum(BPF6(:, repo));
    if (isnan(BPF6(1, repo)/sum(BPF6(:, repo))))
        bfresh_perc(repo, 6) = 0;
    end
    pfresh_perc(repo, 1) = PPF1(1, repo)/sum(PPF1(:, repo));
    if (isnan(PPF1(1, repo)/sum(PPF1(:, repo))))
        pfresh_perc(repo, 1) = 0;
    end
    pfresh_perc(repo, 2) = PPF2(1, repo)/sum(PPF2(:, repo));
    if (isnan(PPF2(1, repo)/sum(PPF2(:, repo))))
        pfresh_perc(repo, 2) = 0;
    end
    pfresh_perc(repo, 3) = PPF3(1, repo)/sum(PPF3(:, repo));
    if (isnan(PPF3(1, repo)/sum(PPF3(:, repo))))
        pfresh_perc(repo, 3) = 0;
    end
    pfresh_perc(repo, 4) = PPF4(1, repo)/sum(PPF4(:, repo));
    if (isnan(PPF4(1, repo)/sum(PPF4(:, repo))))
        pfresh_perc(repo, 4) = 0;
    end
    pfresh_perc(repo, 5) = PPF5(1, repo)/sum(PPF5(:, repo));
    if (isnan(PPF5(1, repo)/sum(PPF5(:, repo))))
        pfresh_perc(repo, 5) = 0;
    end
    pfresh_perc(repo, 6) =  PPF6(1, repo)/sum(PPF6(:, repo));
    if (isnan(PPF6(1, repo)/sum(PPF6(:, repo))))
        pfresh_perc(repo, 6) = 0;
    end
end


figure

% subplot(2,1,1);
% yyaxis left
bar_handle = bar([ofresh_perc(:,1), ofresh_perc(:,2), ofresh_perc(:,3), ofresh_perc(:,4), ofresh_perc(:,5), ofresh_perc(:,6)]);
% title('Processing threads','fontsize',16)
xlabel('Repository number','fontsize',12) 
ylabel('Processing Message Freshness (100%)','fontsize',12)
ylim([0 1]);
% xlim([17 48]);
set(bar_handle(1),'FaceColor',[0,0.5,1])
set(bar_handle(2),'FaceColor',[0,1,0])




figure

% subplot(2,1,1);
% yyaxis left
bar([mean(nonzeros(ofresh_perc(:,1))), mean(nonzeros(ofresh_perc(:,2))), mean(nonzeros(ofresh_perc(:,3))), mean(nonzeros(ofresh_perc(:,4))), mean(nonzeros(ofresh_perc(:,5))), mean(nonzeros(ofresh_perc(:,6)));
    mean(nonzeros(hfresh_perc(:,1))), mean(nonzeros(hfresh_perc(:,2))), mean(nonzeros(hfresh_perc(:,3))), mean(nonzeros(hfresh_perc(:,4))), mean(nonzeros(hfresh_perc(:,5))), mean(nonzeros(hfresh_perc(:,6)));
    mean(nonzeros(bfresh_perc(:,1))), mean(nonzeros(bfresh_perc(:,2))), mean(nonzeros(bfresh_perc(:,3))), mean(nonzeros(bfresh_perc(:,4))), mean(nonzeros(bfresh_perc(:,5))), mean(nonzeros(bfresh_perc(:,6)));
    mean(nonzeros(pfresh_perc(:,1))), mean(nonzeros(pfresh_perc(:,2))), mean(nonzeros(pfresh_perc(:,3))), mean(nonzeros(pfresh_perc(:,4))), mean(nonzeros(pfresh_perc(:,5))), mean(nonzeros(pfresh_perc(:,6)))]);
% title('Processing threads','fontsize',16)
xlabel('Scenario Number','fontsize',12) 
ylabel('Processing Message Freshness (100%)','fontsize',12)
ylim([0 1]);



figure

% yyaxis left
y = [mean(nonzeros(oproc_up(:,1))), mean(nonzeros(oproc_up(:,2))), mean(nonzeros(oproc_up(:,3))), mean(nonzeros(oproc_up(:,4))), mean(nonzeros(oproc_up(:,5))), mean(nonzeros(oproc_up(:,6)));
    mean(nonzeros(ofresh_perc(:,1))), mean(nonzeros(ofresh_perc(:,2))), mean(nonzeros(ofresh_perc(:,3))), mean(nonzeros(ofresh_perc(:,4))), mean(nonzeros(ofresh_perc(:,5))), mean(nonzeros(ofresh_perc(:,6)));
    mean(nonzeros(hproc_up(:,1))), mean(nonzeros(hproc_up(:,2))), mean(nonzeros(hproc_up(:,3))), mean(nonzeros(hproc_up(:,4))), mean(nonzeros(hproc_up(:,5))), mean(nonzeros(hproc_up(:,6)));
    mean(nonzeros(hfresh_perc(:,1))), mean(nonzeros(hfresh_perc(:,2))), mean(nonzeros(hfresh_perc(:,3))), mean(nonzeros(hfresh_perc(:,4))), mean(nonzeros(hfresh_perc(:,5))), mean(nonzeros(hfresh_perc(:,6)));
    mean(nonzeros(bproc_up(:,1))), mean(nonzeros(bproc_up(:,2))), mean(nonzeros(bproc_up(:,3))), mean(nonzeros(bproc_up(:,4))), mean(nonzeros(bproc_up(:,5))), mean(nonzeros(bproc_up(:,6)));
    mean(nonzeros(bfresh_perc(:,1))), mean(nonzeros(bfresh_perc(:,2))), mean(nonzeros(bfresh_perc(:,3))), mean(nonzeros(bfresh_perc(:,4))), mean(nonzeros(bfresh_perc(:,5))), mean(nonzeros(bfresh_perc(:,6)));
    mean(nonzeros(pproc_up(:,1))), mean(nonzeros(pproc_up(:,2))), mean(nonzeros(pproc_up(:,3))), mean(nonzeros(pproc_up(:,4))), mean(nonzeros(pproc_up(:,5))), mean(nonzeros(pproc_up(:,6)))
    mean(nonzeros(pfresh_perc(:,1))), mean(nonzeros(pfresh_perc(:,2))), mean(nonzeros(pfresh_perc(:,3))), mean(nonzeros(pfresh_perc(:,4))), mean(nonzeros(pfresh_perc(:,5))), mean(nonzeros(pfresh_perc(:,6)))];
bar(y,'group')
set(gca,'xtick',1:8,'XTickLabel',{'Processed', 'Fresh', 'Processed', 'Fresh', 'Processed', 'Fresh', 'Processed', 'Fresh'}, 'fontsize', 10)
% xt= [1-0.34, 1-0.2, 1-0.07, 1.07, 1.2, 1.34, 3-0.34, 3-0.2, 3-0.07, 3.07, 3.2, 3.34, 5-0.34, 5-0.2, 5-0.07, 5.07, 5.2, 5.34, 7-0.34, 7-0.2, 7-0.07, 7.07, 7.2, 7.34];
% yt=[y(1,:), y(3,:), y(5,:), y(7,:)]+50;
%xt=[1, 3, 5, 7]-0.25;
%yt=[y(1,1); y(3,1); y(5,1); y(7,1)]+50;
% ytxt=num2str([y(1, :)'; y(3, :)'; y(5, :)'; y(7, :)'],'%.1f');
% text(xt,yt,ytxt,'rotation',90,'fontsize',8,'fontweight','bold')
% title('Processing threads','fontsize',16)
% xlabel('Scenario Number','fontsize',12) 
ylabel('Satisfied Processing Message Upload (100%)','fontsize',12)

groupX = [2 4 6 8]; %// central value of each group
groupY = -0.1; %// vertical position of texts. Adjust as needed
deltaY = .03; %// controls vertical compression of axis. Adjust as needed
groupNames = {'Offices', 'Homes', 'Buses' 'Cars'}; %// note different lengths to test centering
for g = 1:numel(groupX)
    h = text(groupX(g), groupY, groupNames(g), 'Fontsize', 13, 'Fontweight', 'bold');
        %// create text for group with appropriate font size and weight
    pos = get(h, 'Position');
    ext = get(h, 'Extent');
    pos(1) = pos(1) - ext(3)/2; %// horizontally correct position to make it centered
    set(h, 'Position', pos); %// set corrected position for text
end
pos = get(gca, 'Position');
pos(2) = pos(2) + deltaY; %// vertically compress axis to make room for texts
set(gca, 'Position', pos); %/ set corrected position for axis


% yyaxis right
% y = [mean(nonzeros(oproc_up(:,1))), mean(nonzeros(oproc_up(:,2))), mean(nonzeros(oproc_up(:,3))), mean(nonzeros(oproc_up(:,4))), mean(nonzeros(oproc_up(:,5))), mean(nonzeros(oproc_up(:,6)));
%     mean(nonzeros(ofresh_perc(:,1))), mean(nonzeros(ofresh_perc(:,2))), mean(nonzeros(ofresh_perc(:,3))), mean(nonzeros(ofresh_perc(:,4))), mean(nonzeros(ofresh_perc(:,5))), mean(nonzeros(ofresh_perc(:,6)));
%     mean(nonzeros(hproc_up(:,1))), mean(nonzeros(hproc_up(:,2))), mean(nonzeros(hproc_up(:,3))), mean(nonzeros(hproc_up(:,4))), mean(nonzeros(hproc_up(:,5))), mean(nonzeros(hproc_up(:,6)));
%     mean(nonzeros(hfresh_perc(:,1))), mean(nonzeros(hfresh_perc(:,2))), mean(nonzeros(hfresh_perc(:,3))), mean(nonzeros(hfresh_perc(:,4))), mean(nonzeros(hfresh_perc(:,5))), mean(nonzeros(hfresh_perc(:,6)));
%     mean(nonzeros(bproc_up(:,1))), mean(nonzeros(bproc_up(:,2))), mean(nonzeros(bproc_up(:,3))), mean(nonzeros(bproc_up(:,4))), mean(nonzeros(bproc_up(:,5))), mean(nonzeros(bproc_up(:,6)));
%     mean(nonzeros(bfresh_perc(:,1))), mean(nonzeros(bfresh_perc(:,2))), mean(nonzeros(bfresh_perc(:,3))), mean(nonzeros(bfresh_perc(:,4))), mean(nonzeros(bfresh_perc(:,5))), mean(nonzeros(bfresh_perc(:,6)));
%     mean(nonzeros(pproc_up(:,1))), mean(nonzeros(pproc_up(:,2))), mean(nonzeros(pproc_up(:,3))), mean(nonzeros(pproc_up(:,4))), mean(nonzeros(pproc_up(:,5))), mean(nonzeros(pproc_up(:,6)))
%     mean(nonzeros(pfresh_perc(:,1))), mean(nonzeros(pfresh_perc(:,2))), mean(nonzeros(pfresh_perc(:,3))), mean(nonzeros(pfresh_perc(:,4))), mean(nonzeros(pfresh_perc(:,5))), mean(nonzeros(pfresh_perc(:,6)))];
% bar([1:8],[0,0,0,0,0,0; y(2, :); 0,0,0,0,0,0; y(4, :); 0,0,0,0,0,0; y(6, :); 0,0,0,0,0,0; y(8, :)],'group')
% % set(gca,'xtick',1:8)
% % xt= [2-0.34, 2-0.2, 2-0.07, 2.07, 2.2, 2.34, 4-0.34, 4-0.2, 4-0.07, 4.07, 4.2, 4.34, 6-0.34, 6-0.2, 6-0.07, 6.07, 6.2, 6.34, 8-0.34, 8-0.2, 8-0.07, 8.07, 8.2, 8.34];
% % yt=[y(2,:), y(4,:), y(6,:), y(8,:)]-50;
% %xt=[1, 3, 5, 7]-0.25;
% %yt=[y(1,1); y(3,1); y(5,1); y(7,1)]+50;
% % ytxt=num2str([y(2, :)'; y(4, :)'; y(6, :)'; y(8, :)'],'%.1f');
% % text(xt,yt,ytxt,'rotation',90,'fontsize',8,'fontweight','bold')
% % title('Processing threads','fontsize',16)
% ylabel('Non-processing Message Satisfied Upload (100%)','fontsize',12)


