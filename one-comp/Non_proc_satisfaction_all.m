% Make a function out of this, that takes the officeIoT folder address and
% the number of runs as variables;

clear

OPF1 = dlmread('officeIoT/RSR1', ' ', 0, 1);
HPF1 = dlmread('homeIoT/RSR1', ' ', 0, 1);
BPF1 = dlmread('buses/RSR1', ' ', 0, 1);
PPF1 = dlmread('non-proc_proc/RSR1', ' ', 0, 1);


OPF2 = dlmread('officeIoT/RSR2', ' ', 0, 1);
HPF2 = dlmread('homeIoT/RSR2', ' ', 0, 1);
BPF2 = dlmread('buses/RSR2', ' ', 0, 1);
PPF2 = dlmread('non-proc_proc/RSR2', ' ', 0, 1);


OPF3 = dlmread('officeIoT/RSR3', ' ', 0, 1);
HPF3 = dlmread('homeIoT/RSR3', ' ', 0, 1);
BPF3 = dlmread('buses/RSR3', ' ', 0, 1);
PPF3 = dlmread('non-proc_proc/RSR3', ' ', 0, 1);


OPF4 = dlmread('officeIoT/RSR4', ' ', 0, 1);
HPF4 = dlmread('homeIoT/RSR4', ' ', 0, 1);
BPF4 = dlmread('buses/RSR4', ' ', 0, 1);
PPF4 = dlmread('non-proc_proc/RSR4', ' ', 0, 1);


OPF5 = dlmread('officeIoT/RSR5', ' ', 0, 1);
HPF5 = dlmread('homeIoT/RSR5', ' ', 0, 1);
BPF5 = dlmread('buses/RSR5', ' ', 0, 1);
PPF5 = dlmread('non-proc_proc/RSR5', ' ', 0, 1);


OPF6 = dlmread('officeIoT/RSR6', ' ', 0, 1);
HPF6 = dlmread('homeIoT/RSR6', ' ', 0, 1);
BPF6 = dlmread('buses/RSR6', ' ', 0, 1);
PPF6 = dlmread('non-proc_proc/RSR6', ' ', 0, 1);


[r3, c3] = size(OPF1);

for repo = 1:c3
    osat_perc(repo, 1) = OPF1(1, repo)/sum(OPF1(:, repo));
    if (isnan(OPF1(1, repo)/sum(OPF1(:, repo))))
        osat_perc(repo, 1) = 0;
    end
    osat_perc(repo, 2) = OPF2(1, repo)/sum(OPF2(:, repo));
    if (isnan(OPF2(1, repo)/sum(OPF2(:, repo))))
        osat_perc(repo, 2) = 0;
    end
    osat_perc(repo, 3) = OPF3(1, repo)/sum(OPF3(:, repo));
    if (isnan(OPF3(1, repo)/sum(OPF3(:, repo))))
        osat_perc(repo, 3) = 0;
    end
    osat_perc(repo, 4) = OPF4(1, repo)/sum(OPF4(:, repo));
    if (isnan(OPF4(1, repo)/sum(OPF4(:, repo))))
        osat_perc(repo, 4) = 0;
    end
    osat_perc(repo, 5) = OPF5(1, repo)/sum(OPF5(:, repo));
    if (isnan(OPF5(1, repo)/sum(OPF5(:, repo))))
        osat_perc(repo, 5) = 0;
    end
    osat_perc(repo, 6) =  OPF6(1, repo)/sum(OPF6(:, repo));
    if (isnan(OPF6(1, repo)/sum(OPF6(:, repo))))
        osat_perc(repo, 6) = 0;
    end
    hsat_perc(repo, 1) = HPF1(1, repo)/sum(HPF1(:, repo));
    if (isnan(HPF1(1, repo)/sum(HPF1(:, repo))))
        hsat_perc(repo, 1) = 0;
    end
    hsat_perc(repo, 2) = HPF2(1, repo)/sum(HPF2(:, repo));
    if (isnan(HPF2(1, repo)/sum(HPF2(:, repo))))
        hsat_perc(repo, 2) = 0;
    end
    hsat_perc(repo, 3) = HPF3(1, repo)/sum(HPF3(:, repo));
    if (isnan(HPF3(1, repo)/sum(HPF3(:, repo))))
        hsat_perc(repo, 3) = 0;
    end
    hsat_perc(repo, 4) = HPF4(1, repo)/sum(HPF4(:, repo));
    if (isnan(HPF4(1, repo)/sum(HPF4(:, repo))))
        hsat_perc(repo, 4) = 0;
    end
    hsat_perc(repo, 5) = HPF5(1, repo)/sum(HPF5(:, repo));
    if (isnan(HPF5(1, repo)/sum(HPF5(:, repo))))
        hsat_perc(repo, 5) = 0;
    end
    hsat_perc(repo, 6) =  HPF6(1, repo)/sum(HPF6(:, repo));
    if (isnan(HPF6(1, repo)/sum(HPF6(:, repo))))
        hsat_perc(repo, 6) = 0;
    end
    bsat_perc(repo, 1) = BPF1(1, repo)/sum(BPF1(:, repo));
    if (isnan(BPF1(1, repo)/sum(BPF1(:, repo))))
        bsat_perc(repo, 1) = 0;
    end
    bsat_perc(repo, 2) = BPF2(1, repo)/sum(BPF2(:, repo));
    if (isnan(BPF2(1, repo)/sum(BPF2(:, repo))))
        bsat_perc(repo, 2) = 0;
    end
    bsat_perc(repo, 3) = BPF3(1, repo)/sum(BPF3(:, repo));
    if (isnan(BPF3(1, repo)/sum(BPF3(:, repo))))
        bsat_perc(repo, 3) = 0;
    end
    bsat_perc(repo, 4) = BPF4(1, repo)/sum(BPF4(:, repo));
    if (isnan(BPF4(1, repo)/sum(BPF4(:, repo))))
        bsat_perc(repo, 4) = 0;
    end
    bsat_perc(repo, 5) = BPF5(1, repo)/sum(BPF5(:, repo));
    if (isnan(BPF5(1, repo)/sum(BPF5(:, repo))))
        bsat_perc(repo, 5) = 0;
    end
    bsat_perc(repo, 6) =  BPF6(1, repo)/sum(BPF6(:, repo));
    if (isnan(BPF6(1, repo)/sum(BPF6(:, repo))))
        bsat_perc(repo, 6) = 0;
    end
    psat_perc(repo, 1) = PPF1(1, repo)/sum(PPF1(:, repo));
    if (isnan(PPF1(1, repo)/sum(PPF1(:, repo))))
        psat_perc(repo, 1) = 0;
    end
    psat_perc(repo, 2) = PPF2(1, repo)/sum(PPF2(:, repo));
    if (isnan(PPF2(1, repo)/sum(PPF2(:, repo))))
        psat_perc(repo, 2) = 0;
    end
    psat_perc(repo, 3) = PPF3(1, repo)/sum(PPF3(:, repo));
    if (isnan(PPF3(1, repo)/sum(PPF3(:, repo))))
        psat_perc(repo, 3) = 0;
    end
    psat_perc(repo, 4) = PPF4(1, repo)/sum(PPF4(:, repo));
    if (isnan(PPF4(1, repo)/sum(PPF4(:, repo))))
        psat_perc(repo, 4) = 0;
    end
    psat_perc(repo, 5) = PPF5(1, repo)/sum(PPF5(:, repo));
    if (isnan(PPF5(1, repo)/sum(PPF5(:, repo))))
        psat_perc(repo, 5) = 0;
    end
    psat_perc(repo, 6) =  PPF6(1, repo)/sum(PPF6(:, repo));
    if (isnan(PPF6(1, repo)/sum(PPF6(:, repo))))
        psat_perc(repo, 6) = 0;
    end
end


figure

% subplot(2,1,1);
% yyaxis left
bar_handle = bar([osat_perc(:,1), osat_perc(:,2), osat_perc(:,3), osat_perc(:,4), osat_perc(:,5), osat_perc(:,6)]);
% title('Processing threads','fontsize',16)
xlabel('Repository number','fontsize',12) 
ylabel('Percentage (*100%) of non-processing messages satisfied','fontsize',12)
ylim([0 1]);
% xlim([17 48]);
set(bar_handle(1),'FaceColor',[0,0.5,1])
set(bar_handle(2),'FaceColor',[0,1,0])




figure

% subplot(2,1,1);
% yyaxis left
bar([mean(nonzeros(osat_perc(:,1))), mean(nonzeros(osat_perc(:,2))), mean(nonzeros(osat_perc(:,3))), mean(nonzeros(osat_perc(:,4))), mean(nonzeros(osat_perc(:,5))), mean(nonzeros(osat_perc(:,6)));
    mean(nonzeros(hsat_perc(:,1))), mean(nonzeros(hsat_perc(:,2))), mean(nonzeros(hsat_perc(:,3))), mean(nonzeros(hsat_perc(:,4))), mean(nonzeros(hsat_perc(:,5))), mean(nonzeros(hsat_perc(:,6)));
    mean(nonzeros(bsat_perc(:,1))), mean(nonzeros(bsat_perc(:,2))), mean(nonzeros(bsat_perc(:,3))), mean(nonzeros(bsat_perc(:,4))), mean(nonzeros(bsat_perc(:,5))), mean(nonzeros(bsat_perc(:,6)));
    mean(nonzeros(psat_perc(:,1))), mean(nonzeros(psat_perc(:,2))), mean(nonzeros(psat_perc(:,3))), mean(nonzeros(psat_perc(:,4))), mean(nonzeros(psat_perc(:,5))), mean(nonzeros(psat_perc(:,6)))]);
% title('Processing threads','fontsize',16)
xlabel('Scenario Number','fontsize',12) 
ylabel('Non-processing Message Satisfaction (100%)','fontsize',12)
ylim([0 1]);
%TODO:
% Modify this:
lgd1 =legend('1: 1; 1.1; 100; 3,1 \newline2: 0.2; 3; 3; 100; \newline3: 0.3; 3; 4; 100 \newline4: 100; 4.1', '1: 1; 1.1; 100; 3,1 \newline2: 0.2; 3; 3; 100; \newline3: 0.3; 3; 4; 100 \newline4: 100; 3:1', '1: 1; 1.1; 100; 3,1 \newline2: 0.2; 3; 3; 100; \newline3: 0.3; 3; 4; 100 \newline4: 100; 5:2', '1: 1; 1.1; 100; 3,1 \newline2: 0.2; 3; 3; 100; \newline3: 0.3; 3; 4; 100 \newline4: 100; 4:2', '1: 1; 1.1; 100; 3,1 \newline2: 0.2; 3; 3; 100; \newline3: 0.3; 3; 4; 100 \newline4: 100; 2:2', '1: 1; 1.1; 100; 3,1 \newline2: 0.2; 3; 3; 100; \newline3: 0.3; 3; 4; 100 \newline4: 100; 2:1', 'Location', 'southoutside');
title(lgd1, 'Non-Proc:Proc');
lgd1.NumColumns = 6;
% xlim([17 48]);
% set(bar_handle(1),'FaceColor',[0,0.5,1])
% set(bar_handle(2),'FaceColor',[0,1,0])


% procApp2.delay = 0.1
% procApp2.freshPer = [1; 1; 1; 1.5; 2; 3; 1]
% procApp2.procShelf = [1.1; 1.3; 2; 1.6; 2.2; 3.3; 2]
% procApp2.nonprocShelf = [100; 100; 100; 200; 300; 600]
% procApp2.destinationName = r
% procApp2.procSize = 50k
% # Stored message ratio to processing message (no. of messages to be stored vs. no. of messages to be processed)
% procApp2.compressRate = 1,4
% procApp2.passiveRate = [3,1; 3,1; 3,1; 3,1; 3,1; 4,1]


% procApp2.delay = [0.2; 0.2; 0.2; 0.1; 0.1; 0.1]
% procApp2.freshPer = [3; 3; 3; 2; 2; 2]
% procApp2.procShelf = [3; 3.2; 50]
% procApp2.nonprocShelf = [100; 100; 100; 200; 300; 600]
% procApp2.destinationName = r
% procApp2.procSize = 1M
% # Stored message ratio to processing message (no. of messages to be stored vs. no. of messages to be processed)
% procApp2.compressRate = [2,1; 2,1; 2,2; 2,2; 1,2; 1,2]
% procApp2.passiveRate = 4,1


% procApp.storageMode = [true; true; false; false; false; false]
% procApp.maxStorTime = [3000; 3000]
% 
% 
% procApp3.interval = 0.7
% procApp3.delay = [0.3; 0.3; 0.3; 0.3; 0.1; 0.1]
% procApp3.freshPer = [3; 2; 2]
% procApp3.procShelf = [4; 2.2; 3]
% procApp3.nonprocShelf = [100; 500; 100; 300; 600; 900]
% procApp3.destinationName = r
% procApp3.procSize = 1M
% # Stored message ratio to processing message (no. of messages to be stored vs. no. of messages to be processed)
% procApp3.compressRate = 3,1
% procApp3.passiveRate = [4,1]
% procApp3.passive = false
% 
% 
% procApp2.delay = 0.1
% procApp2.freshPer = 1
% procApp2.procShelf = 2
% procApp3.nonprocShelf = [100; 500; 100; 300; 600; 900]
% procApp2.destinationName = r
% procApp2.procSize = 500k
% # Stored message ratio to processing message (no. of messages to be stored vs. no. of messages to be processed)
% procApp2.compressRate = 2,2
% procApp2.passiveRate = [4,1; 3,1; 5,2; 4,2; 2,2; 1,2]
% procApp2.passive = false


